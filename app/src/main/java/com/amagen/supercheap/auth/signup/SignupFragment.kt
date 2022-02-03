package com.amagen.supercheap.auth.signup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.amagen.supercheap.MainActivityApplication
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.FragmentSignupBinding
import com.amagen.supercheap.extensions.isEmailValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings


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

    }

    private fun localSignupWithEmail() {
        if(binding.tvEmail.text.toString().trim().isEmailValid()){
            if(binding.tvPassword.text?.length!! in 5..16) {

                   mAuth.createUserWithEmailAndPassword(binding.tvEmail.text?.trim().toString(),binding.tvPassword.text.toString())
                       .addOnFailureListener {

                           binding.tvEmailVerificationStatus.text = it.localizedMessage
                           binding.tvEmailVerificationStatus.setTextColor(Color.RED)

                       }
                       .addOnCompleteListener {
                           if(it.isSuccessful)
                                mAuth.currentUser?.sendEmailVerification()
                                    ?.addOnSuccessListener {
                                        binding.tvEmailVerificationStatus.setTextColor(Color.BLACK)
                                        binding.tvEmailVerificationStatus.text = getString(R.string.verification_status)+getString(R.string.verification_email_sent)
                                        binding.btnWhenEmailVerified.visibility=View.VISIBLE
                                        binding.btnWhenEmailVerified.setOnClickListener {
                                            if(mAuth.currentUser?.isEmailVerified!!){
                                                mAuth.signInWithEmailAndPassword(binding.tvEmail.toString().trim(),binding.tvPassword.toString().trim())
                                                    .addOnCompleteListener {
                                                        Toast.makeText(requireContext(),"Welcome "+mAuth.currentUser?.displayName,Toast.LENGTH_SHORT).show()
                                                        startActivity(Intent(activity,MainActivityApplication::class.java))
                                                        activity?.finish()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                                                    }

                                            }
                                            else{
                                                Toast.makeText(requireContext(),"Please verify your account",Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    }
                                    ?.addOnFailureListener {
                                        Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                                    }

                       }
            }else{
                Toast.makeText(activity,"password must contains 6 characters", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(activity,"invalid email please try again", Toast.LENGTH_SHORT).show()
        }
        binding.pbSignup.visibility= View.GONE
    }


}