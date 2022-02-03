package com.amagen.supercheap.extensions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.amagen.supercheap.R
import com.amagen.supercheap.models.Elements
import java.util.regex.Matcher
import java.util.regex.Pattern


fun CharSequence?.isEmailValid()=!isNullOrEmpty()&&Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun CharSequence?.isPhoneValid() = !isNullOrEmpty()&&Patterns.PHONE.matcher(this).matches()

fun Activity?.hideKeyBoard(){
    this?.getSystemService(InputMethodManager::class.java)?.hideSoftInputFromWindow(this?.currentFocus?.windowToken,0)
}
fun Context.setDialogIfApplicationLoadingData(loading: LiveData<Boolean>,dialog:Dialog,lifecycleOwner: LifecycleOwner){

    dialog.setContentView(R.layout.loading_layout)
    dialog.findViewById<WebView>(R.id.wv_animation_presentation).loadUrl("file:///android_asset/index.html")
    dialog.hideCorners()
    loading.observe(lifecycleOwner, Observer {
        if(it){
            dialog.show()
            Log.d("dbchecker", "show $it")
        }else{
            Log.d("dbchecker", "dismiss $it")
            dialog.dismiss()

        }
    })

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

