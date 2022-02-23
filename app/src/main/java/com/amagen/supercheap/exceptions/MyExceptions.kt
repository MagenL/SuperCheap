package com.amagen.supercheap.exceptions

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import com.amagen.supercheap.R
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
    }
}