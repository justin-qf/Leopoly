package com.app.leopoly.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.provider.Telephony
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.app.leopoly.R
import com.app.leopoly.dialogs.DialogToast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object HELPER {
    var dialog: Dialog? = null
    var format = DecimalFormat("0.00")

    @JvmStatic
    fun print(tag: String?, message: String?) {
        Log.e(tag, message!!)
    }

    @JvmStatic
    fun printData(tag: String?, message: String?) {
        print("Api: ", tag)
        print("Method: ", message)
    }

    @JvmStatic
    fun apiLog(apiName: String?, method: String?, data: Map<String, String>) {
        print("Api: ", apiName)
        print("Post: ", method)
        print("Param: ", data.toString())
    }

    fun SIMPLE_ROUTE(act: Activity, routeName: Class<*>?) {
        val i = Intent(act, routeName)
        act.startActivity(i)
        act.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }

    @JvmStatic
    fun clearText(textView: TextInputEditText) {
        textView.setText("")
    }

    @JvmStatic
    fun setTextColour(activity: Activity?, textView: TextView, id: Int?) {
        textView.setTextColor(ContextCompat.getColor(activity!!, id!!))
    }

    @JvmStatic
    fun setTextColourWithText(textView: TextView, context: Context?) {
        textView.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
    }

    fun setImageColour(imgView: ImageView, context: Context?) {
        imgView.setColorFilter(
            ContextCompat.getColor(
                context!!,
                R.color.colorWhite
            )
        )
    }

    fun setPasswordToggle( passwordEdittext:TextInputEditText){
        // Set a touch listener for the password input field
        passwordEdittext.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2 // Index for drawableEnd (right drawable)
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (passwordEdittext.right - passwordEdittext.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    // Toggle password visibility when the drawableEnd (eye icon) is clicked
                    if (passwordEdittext.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        // Password is currently hidden, so show it
                        passwordEdittext.inputType = InputType.TYPE_CLASS_TEXT
                        passwordEdittext.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.eye_invisible,
                            0
                        )
                    } else {
                        // Password is currently visible, so hide it
                        passwordEdittext.inputType =
                            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        passwordEdittext.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.eye_visible,
                            0
                        )
                    }
                    // Move the cursor to the end of the text
                    passwordEdittext.setSelection(passwordEdittext.text!!.length)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    fun setLayoutBgColour(layout: ConstraintLayout, context: Context?) {
        layout.setBackgroundColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorBlack
            )
        )
    }


    fun CHANGE_ACTIONBAR_COLOUR(act: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = act.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = act.resources.getColor(R.color.black_overlay)
        }
    }

    fun ROUTE(act: Activity, routeName: Class<*>?) {
        act.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        act.finish()
        val i = Intent(act, routeName)
        act.startActivity(i)
        act.overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_left)
    }

    fun ROUTE_ANIM(act: Activity, routeName: Class<*>?) {
        val i = Intent(act, routeName)
        act.startActivity(i)
        act.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    fun ON_BACK_PRESS(act: Activity) {
        act.finish()
    }

    fun ERROR_HELPER(editText: TextInputEditText, nameEdtLayout: TextInputLayout) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                nameEdtLayout.error = ""
                nameEdtLayout.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setWhiteNavigationBar(dialog: Dialog, act: Activity) {
        val window = dialog.window
        if (window != null) {
            val metrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(metrics)
            val dimDrawable = GradientDrawable()
            // ...customize your dim effect here
            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(act.getColor(R.color.grayTextColor))
            // navigationBarDrawable.setTint(act.getColor(R.color.WhiteColor));
            val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)
            window.setBackgroundDrawable(windowBackground)
        }
    }

    fun dismissLoadingTran() {
        try {
            if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    fun LOAD_HTML(textView: TextView, data: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT)
        } else {
            textView.text = Html.fromHtml(data)
        }
    }

    /**
     * Determine if the device is a tablet (i.e. it has a large screen).
     *
     * @param context The calling context.
     */
    fun isTablet(context: Context): Boolean {
        return ((context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    fun closeKeyboard(view: View?, act: Activity) {
        if (view != null) {
            val manager = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun convertDate(dateStr: String?): String {
        @SuppressLint("SimpleDateFormat") val df: DateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        var result: Date? = null
        var convertedDate = ""
        if (dateStr != null) {
            try {
                result = df.parse(dateStr)
                println("date:$result")
                @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("dd-MMM-yyyy")
                println(sdf.format(result)) // prints date in the format sdf
                convertedDate = sdf.format(result)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return convertedDate
    }

    fun getConsecutiveOTP(message: String?): String {
        var otp = ""
        val p = Pattern.compile("\\d+")
        val m = p.matcher(message)
        while (m.find()) {
            if (m.group().length == 4) {
                otp = m.group()
            }
            break
        }
        return otp
    }

    fun slideEnter(act: Activity) {
        act.overridePendingTransition(
            R.anim.slide_from_right,
            R.anim.slide_to_left
        )
    }

    fun slideExit(act: Activity) {
        act.overridePendingTransition(
            R.anim.slide_from_left,
            R.anim.slide_to_right
        )
    }

    var alertDialog: AlertDialog? = null

    @JvmStatic
    fun hideProgress(context: Context?) {
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
    }

    @JvmStatic
    fun showProgress(context: Activity) {
        hideProgress(context)
        val dialogBuilder = AlertDialog.Builder(context, R.style.NewDialog)
        val inflater = context.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_bar_layout, null)
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create()
        alertDialog!!.show()
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.setCancelable(false)
    }

    fun emailValidator(emailToText: String): Boolean {
        return !emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()
    }

    fun phoneValidator(emailToText: String): Boolean {
        return !emailToText.isEmpty() && Patterns.PHONE.matcher(emailToText).matches()
    }

    fun getFacebookPageURL(context: Context): String {

        //method to get the right URL to use in the intent
        val packageManager = context.packageManager
        return try {
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) {
                "fb://facewebmodal/f?href=" + "http://developers.facebook.com/android"
            } else { //older versions of fb app
                ""
            }
        } catch (e: PackageManager.NameNotFoundException) {
            "http://developers.facebook.com/android" //normal web url
        }
    }

    fun getCurrentDate(): Date {
        return Date()
    }

    fun shareOnMessage(act: Activity, shareContent: String) {
        try {
            val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(act)
            val textIntent = Intent(Intent.ACTION_SEND)
            textIntent.type = "text/plain"
            textIntent.putExtra(Intent.EXTRA_TEXT, shareContent.trim { it <= ' ' })
            if (defaultSmsPackageName != null) {
                textIntent.setPackage(defaultSmsPackageName)
            }
            act.startActivity(textIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareOnWhatsapp(act: Activity, shareContent: String?) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.setPackage("com.whatsapp")
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent)
            sendIntent.type = "text/html"
            act.startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun showDatePickerDialog(act: Activity?, views: TextInputEditText) {
        val selectedDate = Calendar.getInstance()
        val year = selectedDate[Calendar.YEAR]
        val month = selectedDate[Calendar.MONTH]
        val day = selectedDate[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(act!!, { view, year, monthOfYear, dayOfMonth ->
            selectedDate[Calendar.YEAR] = year
            selectedDate[Calendar.MONTH] = monthOfYear
            selectedDate[Calendar.DAY_OF_MONTH] = dayOfMonth
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            views.setText(sdf.format(selectedDate.time))
        }, year, month, day)
        datePickerDialog.show()
    }

    fun formatDate(date: Date, format: String): String {
        val sdf = SimpleDateFormat(format)
        return sdf.format(date)
    }

    fun commonDialog(act: Activity?, msg: String?) {
        val dialogPermission = DialogToast(act!!)
        dialogPermission.show()
        dialogPermission.holder!!.messageLayout.visibility = View.VISIBLE
        dialogPermission.holder!!.tvMessage.text = msg
        dialogPermission.holder!!.bottomBtnLayout.visibility = View.VISIBLE
        dialogPermission.holder!!.btnDialogGet.visibility = View.GONE
        dialogPermission.holder!!.btnDialogLogout.visibility = View.GONE
        dialogPermission.holder!!.btnDialogLogout.setText(R.string.delete)
        Objects.requireNonNull(dialogPermission.holder)!!.btnDialogCancel
            .setOnClickListener { dialogPermission.dismiss() }
        dialogPermission.holder!!.btnDialogLogout
            .setOnClickListener { dialogPermission.dismiss() }
    }

    fun changeEditText(
        act: Activity,
        textInputLayout: TextInputLayout,
        textInputEditText: TextInputEditText,
        isEditable: Boolean
    ) {
        if (isEditable) {
            textInputLayout.isClickable = true
            textInputLayout.isEnabled = true
            textInputEditText.isClickable = true
            textInputEditText.isCursorVisible = true
            textInputEditText.setTextColor(
                ContextCompat.getColor(
                    act,
                    R.color.colorBlack
                )
            )
        } else {
            textInputLayout.isClickable = false
            textInputLayout.isEnabled = false
            textInputEditText.isClickable = false
            textInputEditText.isCursorVisible = false
            textInputEditText.setTextColor(
                ContextCompat.getColor(
                    act,
                    R.color.shadow
                )
            )
        }

    }
}