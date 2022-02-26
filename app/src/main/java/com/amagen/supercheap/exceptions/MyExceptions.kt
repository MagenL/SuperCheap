package com.amagen.supercheap.exceptions

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LifecycleCoroutineScope
import com.amagen.supercheap.MainActivityApplication
import com.amagen.supercheap.MainActivityViewModel
import com.amagen.supercheap.R
import com.amagen.supercheap.extensions.checkConnectivityStatus
import kotlinx.coroutines.CoroutineExceptionHandler

class MyExceptions(
    message: String?,
    cause: Throwable?,
    enableSuppression: Boolean,
    writableStackTrace: Boolean,

) : Throwable(message, cause, enableSuppression, writableStackTrace) {
    companion object{
        fun pythonConnectionException(context:Context):String{
            return context.getString(R.string.internet_fail)
        }
        fun exceptionHandlerForCoroutines(context: Context): CoroutineExceptionHandler {
            return CoroutineExceptionHandler { _, exception ->
                //Toast.makeText(context, pythonConnectionException(context), Toast.LENGTH_SHORT).show()
                println(pythonConnectionException(context))

            }
        }
        fun exceptionHandlerForCoroutinesInViewModel(): CoroutineExceptionHandler {
            return CoroutineExceptionHandler { _, exception ->
                //Toast.makeText(context, pythonConnectionException(context), Toast.LENGTH_SHORT).show()
                println(exception)

            }
        }
        fun exceptionHandlerForNoSupers(activity:MainActivityApplication=MainActivityApplication(),mainActivityViewModel: MainActivityViewModel,lifecycleCoroutineScope: LifecycleCoroutineScope, block:()->Unit):CoroutineExceptionHandler{
            return CoroutineExceptionHandler{_,_->
                activity.checkConnectivityStatus(mainActivityViewModel, lifecycleScope = lifecycleCoroutineScope)

            }
        }
    }
}