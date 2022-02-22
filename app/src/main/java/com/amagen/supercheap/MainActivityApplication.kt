package com.amagen.supercheap

import android.app.Dialog
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.amagen.supercheap.databinding.ActivityMainApplicationBinding
import com.amagen.supercheap.extensions.checkConnectivityStatus
import com.amagen.supercheap.extensions.delayOnLifeCycle
import com.amagen.supercheap.extensions.hideCorners
import com.amagen.supercheap.network.NetworkStatusChecker
import kotlinx.android.synthetic.main.no_internet_alert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivityApplication : AppCompatActivity() {

    private lateinit var binding: ActivityMainApplicationBinding

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main_application)
        navView.setupWithNavController(navController)


        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        //checkConnectivityStatus(false)

        this.checkConnectivityStatus(mainActivityViewModel, lifecycleScope = lifecycleScope,)
        //delete when check is done


    }


//    private fun checkConnectivityStatus(flag:Boolean):Boolean {
//        //check connectivity status
//        val connectivityManager = getSystemService(ConnectivityManager::class.java)
//        if(NetworkStatusChecker(connectivityManager).hasInternetConnection()){
//            mainActivityViewModel.getAllSupers()
//        }else{
//            if(!flag){
//                val dialog= Dialog(this)
//                dialog.hideCorners()
//                dialog.setContentView(R.layout.no_internet_alert)
//                dialog.btn_ok_dialog.setOnClickListener {
//                    dialog.dismiss()
//                }
//                dialog.show()
//            }
//            lifecycleScope.launch {
//                delay(5000)
//                lifecycleScope.launch(Dispatchers.IO) {
//                    if(mainActivityViewModel.db.superTableOfIdAndName().getAllSupers().isEmpty()){
//                        checkConnectivityStatus(true)
//                    }
//                }
//            }
//        }
//        return false
//    }


}