package com.amagen.supercheap

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.amagen.supercheap.databinding.ActivityMainApplicationBinding
import com.amagen.supercheap.extensions.checkConnectivityStatus

class MainActivityApplication : AppCompatActivity() {

    private lateinit var binding: ActivityMainApplicationBinding

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SuperCheap)


        binding = ActivityMainApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main_application)
        navView.setupWithNavController(navController)



        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]


        this.checkConnectivityStatus(mainActivityViewModel, lifecycleScope = lifecycleScope)


        val sharedpref = getSharedPreferences("onboarding", Context.MODE_PRIVATE)
        if(!sharedpref.getBoolean("finish",false)){
            hideNavbar(navController)
        }



    }


    fun hideNavbar(navController: NavController) {
        binding.navView.visibility= View.GONE
        navController.navigate(R.id.action_navigation_home_to_viewPagerFragment)

    }
    public fun showNavBar(){
        binding.navView.visibility= View.VISIBLE
    }




}