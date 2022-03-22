package com.amagen.supercheap.extensions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import androidx.lifecycle.*
import com.amagen.supercheap.MainActivityViewModel
import com.amagen.supercheap.R
import com.amagen.supercheap.network.NetworkStatusChecker
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.no_internet_alert.*
import kotlinx.coroutines.*


fun CharSequence?.isEmailValid()=!isNullOrEmpty()&&Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun CharSequence?.isPhoneValid() = !isNullOrEmpty()&&Patterns.PHONE.matcher(this).matches()

fun Activity?.hideKeyBoard(){
    this?.getSystemService(InputMethodManager::class.java)?.hideSoftInputFromWindow(this?.currentFocus?.windowToken,0)
}
fun Context.setDialogIfApplicationLoadingData(loading: LiveData<Boolean>,dialog:Dialog,lifecycleOwner: LifecycleOwner, progress:LiveData<Int>?=null){

    dialog.setContentView(R.layout.loading_layout)
    dialog.findViewById<WebView>(R.id.wv_animation_presentation).loadUrl("file:///android_asset/index.html")
    dialog.hideCorners()
    dialog.setCancelable(false)

    loading.observe(lifecycleOwner) {
        if (it) {
            dialog.show()
            Log.d("dbchecker", "show $it")
        } else {
            Log.d("dbchecker", "dismiss $it")
            dialog.dismiss()
        }
    }
    progress?.observe(lifecycleOwner){

        dialog.pb_percent.visibility=View.VISIBLE
        dialog.tv_progress.visibility=View.VISIBLE

        dialog.pb_percent.setProgress(it,true)
        val p:String = "${((it / 389.0) * 100).toInt()} %"
        dialog.tv_progress.text=p
//        dialog.tv_progress.text=resources.getText(R.string.progress_precent,((it/389)*100).toString())
    }

}
fun Boolean.toInt() = if (this) 1 else 0


fun Int.useNotToOppsisteZeroAndOne() = if (this==0) 1 else 0

fun Int.fromBoolToInt(boolean: Boolean):Int {
    return if(boolean)
        1
    else
        0
}

fun Dialog.hideCorners(){
    this.window?.setBackgroundDrawableResource(android.R.color.transparent)
}



fun View.delayOnLifeCycle(
    durationInMillis:Long,
    dispatcher:CoroutineDispatcher = Dispatchers.Main,
    view:View
): Job?=findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
    lifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
        view.isEnabled =false
        delay(durationInMillis)
        view.isEnabled=true
    }

}
//?=null,
//
//
fun View.delayOnLifeCycle(
    durationInMillis:Long,
    dispatcher:CoroutineDispatcher = Dispatchers.Main,
    view:View,
    block:()->Unit
): Job?=findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
    lifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
         view.isEnabled = false
        block()
        delay(durationInMillis)
        view.isEnabled = true
    }
}
fun Activity.checkConnectivityStatus(
    mainActivityViewModel:MainActivityViewModel,
    flag:Boolean=false,
    lifecycleScope: LifecycleCoroutineScope,
    activity: Activity=this
):Boolean{
    val connectivityManager = getSystemService(ConnectivityManager::class.java)
    if(NetworkStatusChecker(connectivityManager).hasInternetConnection()){
        mainActivityViewModel.getAllSupers()
        return true
    }else{
        if(!flag){
            noInternetDialog()
        }
        lifecycleScope.launch {
            delay(5000)
            lifecycleScope.launch(Dispatchers.IO) {
                if(mainActivityViewModel.db.superTableOfIdAndName().getAllSupers().isEmpty()){
                    activity.checkConnectivityStatus(mainActivityViewModel,flag = true, lifecycleScope=lifecycleScope, activity = activity)
                }
            }
        }
    }
    return false
}

fun Activity.noInternetDialog() {
    val dialog = Dialog(this)
    dialog.hideCorners()
    dialog.setContentView(R.layout.no_internet_alert)
    dialog.btn_ok_dialog.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
}