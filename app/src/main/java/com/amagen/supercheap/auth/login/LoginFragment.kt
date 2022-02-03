package com.amagen.supercheap.auth.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.amagen.supercheap.MainActivityApplication
import com.amagen.supercheap.R
import com.amagen.supercheap.auth.signup.SignupFragment
import com.amagen.supercheap.databinding.FragmentLoginBinding
import com.amagen.supercheap.extensions.isEmailValid
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        authCheckAndUpdateUI()



        binding.signInButton.setOnClickListener {
            binding.pbGoogleBtn.visibility=View.VISIBLE
            signInWithGoogle()
        }

        binding.signInButtonWithEmail.setOnClickListener {
            binding.pbEmailSignIn.visibility=View.VISIBLE
            signInWithEmailAndPassword()
        }

        binding.goToSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.auth_container, SignupFragment()).addToBackStack(null).commit()
        }

    }

    private fun authCheckAndUpdateUI() {
        if (mAuth.currentUser != null ) {
            if( mAuth.currentUser?.isEmailVerified!!){
                startActivity(Intent(requireContext(), MainActivityApplication::class.java))
                activity?.finish()
            }else{
                Toast.makeText(requireContext(),"email not verified",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun signInWithEmailAndPassword() {


        if(binding.tvEmail.text.toString().trim().isEmailValid()){
            if(binding.tvPassword.text?.length!! >=5) {
                mAuth.signInWithEmailAndPassword(binding.tvEmail.text.toString().trim(),binding.tvPassword.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            authCheckAndUpdateUI()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity,"failed to login "+it.localizedMessage,Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(activity,"password must contains 6 characters",Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(activity,"invalid email please try again",Toast.LENGTH_SHORT).show()
        }

        binding.pbEmailSignIn.visibility=View.GONE
    }

    private fun signInWithGoogle() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = googleSignInClient.signInIntent
        getResult.launch(signInIntent)
    }


    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                println("onactivityresult method")

                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                if (task.isSuccessful) {
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)!!
                        println("success")
                        firebaseAuthWithGoogle(account.idToken!!)
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        println("failure")
                    }

                } else {
                    println(task.exception?.message)
                }


            }
        }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("signInWithCredential:success")

                    startActivity(Intent(activity, MainActivityApplication::class.java))
                    activity?.finish()


                } else {
                    // If sign in fails, display a message to the user.
                    println("signInWithCredential:failure ${task.exception}")

                }
            }
            .addOnFailureListener {
                println(it.message)
                binding.pbGoogleBtn.visibility= View.GONE
            }
    }

}