package com.amagen.supercheap.auth.signup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.amagen.supercheap.MainActivityApplication
import com.amagen.supercheap.R
import com.amagen.supercheap.auth.login.LoginFragment
import com.amagen.supercheap.databinding.FragmentSignupBinding
import com.amagen.supercheap.extensions.isEmailValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.actionCodeSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignupFragment : Fragment() {


    private var _binding:FragmentSignupBinding? =null
    private val binding get() = _binding!!
    private var mAuth:FirebaseAuth= FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpButtonWithEmail.setOnClickListener {
            binding.pbSignup.visibility= View.VISIBLE
            localSignupWithEmail()
        }

        binding.goToSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.auth_container, LoginFragment()).addToBackStack(null).commit()
        }

    }

    private fun localSignupWithEmail() {
        if(binding.tvDisplayname.text.toString().length in 2..10) {
            val nickname = binding.tvDisplayname.text.toString()
            val email = binding.tvEmail.text?.toString()!!.trim()
            val pass = binding.tvPassword.text.toString()
            if (binding.tvEmail.text.toString().trim().isEmailValid()) {
                if (binding.tvPassword.text?.length!! in 5..16) {
                    mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnFailureListener {
                            binding.tvEmailVerificationStatus.text = it.localizedMessage
                            binding.tvEmailVerificationStatus.setTextColor(Color.RED)
                            binding.tvSendVerificationEmailAgain.text = getString(R.string.send_verification_email_again)
                            binding.tvSendVerificationEmailAgain.setOnClickListener {
                                sendVerificationMail(email,pass)
                            }

                        }
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(nickname).build()
                                mAuth.currentUser!!.updateProfile(profileUpdate)
                                sendVerificationMail(email,pass)
                            }else{
                                Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                                println(it.exception!!.localizedMessage)
                            }

                        }
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.password_validation_6chars),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(activity, getString(R.string.invalid_email_try_again), Toast.LENGTH_SHORT)
                    .show()
            }
        }else{
            Toast.makeText(requireContext(), getString(R.string.nickname_validation_lenght), Toast.LENGTH_SHORT).show()
        }
        binding.pbSignup.visibility = View.GONE
    }

    private fun sendVerificationMail(email: String,pass:String) {
        mAuth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                binding.tvEmailVerificationStatus.setTextColor(Color.BLACK)
                binding.tvEmailVerificationStatus.text =
                    getString(R.string.verification_status) + getString(R.string.verification_email_sent)
                binding.btnWhenEmailVerified.visibility = View.VISIBLE
                binding.btnWhenEmailVerified.setOnClickListener {
                    mAuth.currentUser!!.reload().addOnCompleteListener {
                        if (mAuth.currentUser?.isEmailVerified!!) {
                            mAuth.signInWithEmailAndPassword(email, pass)
                                .addOnCompleteListener {
                                    if (it.isComplete) {
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.welcome_space) + mAuth.currentUser?.displayName,
                                            Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(activity, MainActivityApplication::class.java))
                                        activity?.finish()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.please_verify_your_account),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

            }
            ?.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    it.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}