package com.amagen.supercheap.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amagen.supercheap.MainActivityApplication
import com.amagen.supercheap.R
import com.amagen.supercheap.auth.login.LoginFragment
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)
        if(FirebaseAuth.getInstance().currentUser!=null){
            startActivity(Intent(this, MainActivityApplication::class.java))
            this.finish()
        }else{
            setTheme(R.style.Theme_SuperCheap)
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction().replace(R.id.auth_container, LoginFragment()).commitNow()
            }
        }

    }
}