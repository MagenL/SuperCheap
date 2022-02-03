package com.amagen.supercheap.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amagen.supercheap.R
import com.amagen.supercheap.auth.login.LoginFragment

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.auth_container, LoginFragment()).commitNow()
        }
    }
}