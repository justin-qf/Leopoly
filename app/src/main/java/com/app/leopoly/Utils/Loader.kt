package com.app.ssn.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.view.WindowManager.BadTokenException
import android.widget.Toast
import com.app.leopoly.R
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Admin on 19-01-2018.
 */

@SuppressLint("StaticFieldLeak")
object Loader {
    var activity: Activity? = null
    private lateinit var alertDialog: AlertDialog
    fun showProgress(context: Activity) {
        hideProgress()
        val dialogBuilder = AlertDialog.Builder(context, R.style.NewDialog)
        val inflater = context.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        context.runOnUiThread(Runnable { // Showing Alert Message
            try {
                if (!alertDialog.isShowing) {
                    alertDialog.show()
                }
            } catch (e: BadTokenException) {
                e.printStackTrace()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        })
        alertDialog.apply {
            show()
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
    }

    private fun hideProgress() {
        if (Loader::alertDialog.isInitialized && alertDialog.isShowing) {
            alertDialog.dismiss()
        }
    }

    fun showError(view: View, message: String?) {
        Snackbar.make(view, message.toString(), Snackbar.LENGTH_SHORT).show()
    }

    fun showToast(context: Activity, text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun showToastShort(context: Activity, text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

}
