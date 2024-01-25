package com.app.leopoly.common

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.leopoly.R
import com.app.leopoly.interfaces.OnAlertDialogDismiss
import com.google.android.material.snackbar.Snackbar
import java.util.*

object Utility {
    private var dialog: ProgressDialog? = null
    @JvmStatic
    fun Log(act: String?, msg: Any) {
        android.util.Log.e(act, msg.toString() + "")
    }

    fun RemoveError(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                editText.error = null
            }

            override fun afterTextChanged(editable: Editable) {}
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
        })
    }

    @JvmStatic
    fun   showAlert(act: Activity, msg: String?) {
        AlertDialog.Builder(act)
            .setMessage(msg)
            .setCancelable(true)
            .setPositiveButton("OK") { dialogInterface, i ->
                dialogInterface.dismiss()
                (act as OnAlertDialogDismiss).onDialogDismiss("")
            }
            .show()
    }

    @JvmStatic
    fun showAlertNew(act: Activity?, msg: String?) {
        AlertDialog.Builder(act)
            .setMessage(msg)
            .setCancelable(true)
            .setPositiveButton("OK") { dialogInterface, i -> dialogInterface.dismiss() }
            .show()
    }

    @JvmStatic
    fun showAlert(act: Activity, msg: String?, flag: String?) {
        AlertDialog.Builder(act)
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("OK") { dialogInterface, i ->
                dialogInterface.dismiss()
                (act as OnAlertDialogDismiss).onDialogDismiss(flag)
            }
            .show()
    }

    fun showFragmentDialog(context: Fragment, flag: String?, message: String?) {
        AlertDialog.Builder(context.context)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton("OK") { dialogInterface, i ->
                dialogInterface.dismiss()
                (context as OnAlertDialogDismiss).onDialogDismiss(flag)
            }
            .show()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    @JvmStatic
    fun showLoading(act: Activity) {
        if (dialog != null && dialog!!.isShowing) return
        dialog = ProgressDialog(act)
        dialog!!.setMessage("Loading...")
        act.runOnUiThread { // Showing Alert Message
            try {
                if (dialog != null && !dialog!!.isShowing) dialog!!.show()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun dismissLoading() {
        try {
            if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun showSnackBar(view: View?, act: Activity?, message: String?) {
        val snackbar = Snackbar
            .make(view!!, message!!, Snackbar.LENGTH_LONG)
            .setAction("OK") { }
        snackbar.setActionTextColor(ContextCompat.getColor(act!!, R.color.colorWhite))
        snackbar.show()
    }

    fun convertFirstUpper(str: String?): String? {
        if (str == null || str.isEmpty()) {
            return str
        }
        Log("FirstLetter", str.substring(0, 1) + "    " + str.substring(1))
        return str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1)
    }
}