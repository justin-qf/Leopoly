package com.app.leopoly.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.app.leopoly.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.net.MalformedURLException
import java.net.URL
import java.text.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object CodeReUse {
    // Notification
    const val CHANNEL_ID = "my_channel_01"
    const val CHANNEL_NAME = "iWINGZY"
    const val CHANNEL_DESCRIPTION = "com.app.iwingzy"
    const val GET_FORM_HEADER = 0
    const val GET_JSON_HEADER = 1
    const val CAMERA_INTENT = 101
    const val GALLERY_INTENT = 102
    const val ASK_PERMISSSION = 103
    const val SELECT_VIDEO_GALLERY = 104
    const val SELECT_VIDEO_CAMERA = 105
    var df = DecimalFormat("##.###")
    var daysCount = ""

    @SuppressLint("DefaultLocale")
    var formatterLakhs: NumberFormat = DecimalFormat("00,00,000")
    var formatterCrores: NumberFormat = DecimalFormat("0,00,00,000")
    var datef: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&*+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    //check is email is valid or not
    @JvmStatic
    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    //check is contact valid or not
    @JvmStatic
    fun isContactValid(mobileNumber: String?): Boolean {
        return if (mobileNumber == null) {
            false
        } else if (mobileNumber.isEmpty()) {
            false
        } else if (mobileNumber.length < 10) {
            false
        } else mobileNumber.length <= 10
    }

    @JvmStatic
    fun hideKeyboard(activity: Activity, view: View) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setTimePickerDialog(act: Activity?, editText: TextInputEditText) {
        val picker: TimePickerDialog
        val cldr = Calendar.getInstance()
        val hour = cldr[Calendar.HOUR_OF_DAY]
        val minutes = cldr[Calendar.MINUTE]
        // time picker dialog
        picker = TimePickerDialog(act,
            { tp, sHour, sMinute ->
                var hour = sHour
                val timeSet: String
                if (hour > 12) {
                    hour -= 12
                    timeSet = "PM"
                } else if (hour == 0) {
                    hour += 12
                    timeSet = "AM"
                } else if (hour == 12) {
                    timeSet = "PM"
                } else {
                    timeSet = "AM"
                }
                val min: String
                min = if (sMinute < 10) "0$sMinute" else sMinute.toString()
                val aTime = "$hour:$min $timeSet"
                Log.e("SELECT_TIME", aTime)
                editText.setText(aTime)
            }, hour, minutes, false
        )
        picker.show()
    }

    @JvmStatic
    fun datePicker(act: Activity?, editText: TextInputEditText) {
        val picker: DatePickerDialog
        val cldr = Calendar.getInstance()
        val day = cldr[Calendar.DAY_OF_MONTH]
        val month = cldr[Calendar.MONTH]
        val year = cldr[Calendar.YEAR]
        picker = DatePickerDialog(
            act!!,
            { view, year, month, dayOfMonth ->
                var month = month
                month = month + 1
                var dateStr = ""
                var monthStr = ""
                dateStr = if (dayOfMonth < 10) {
                    "0$dayOfMonth"
                } else {
                    dayOfMonth.toString()
                }
                monthStr = if (month < 10) {
                    "0$month"
                } else {
                    month.toString()
                }
                editText.setText("$dateStr-$monthStr-$year")
                Log.e("SELECT_DATE", "$dateStr-$monthStr-$year")
            }, year, month, day
        )
        picker.datePicker.calendarViewShown = false
        picker.datePicker.spinnersShown = true
        picker.show()
    }

    @JvmStatic
    fun activityBackPress(act: Activity) {
        act.finish()
        act.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
    }

    @JvmStatic
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

    @JvmStatic
    fun RemoveError(editText: EditText, textInputLayout: TextInputLayout) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                editText.error = null
                textInputLayout.error = null
                textInputLayout.helperText = ""
            }

            override fun afterTextChanged(editable: Editable) {}
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
        })
    }

    //get each word capitalize
    fun capitalizeString(str: String): String {
        val words = str.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val capitalizeWord = StringBuilder()
        for (w in words) {
            val first = w.substring(0, 1)
            val afterfirst = w.substring(1)
            capitalizeWord.append(first.uppercase(Locale.getDefault())).append(afterfirst)
                .append(" ")
        }
        return capitalizeWord.toString().trim { it <= ' ' }
    }

    fun getDateFromDateTime(wantDate: Boolean, dateTimeStr: String?): String {
        //true  return date
        //false return time
        try {
            //DateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            val f: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val d = dateTimeStr?.let { f.parse(it) }
            val date: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            val time: DateFormat = SimpleDateFormat("hh:mm:ss a")
            return if (wantDate) date.format(d) else time.format(d)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun makeCall(act: Activity, contactNumber: String): Intent {
        val returnDate = Intent()
        returnDate.putExtra("mobileNumber", contactNumber)
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$contactNumber")
        if (ActivityCompat.checkSelfPermission(
                act,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            returnDate.putExtra("returnCode", -1)
            return returnDate
        } else {
            returnDate.putExtra("returnCode", 1)
        }
        act.startActivity(intent)
        return returnDate
    }

    fun getFilenameFromURL(url: String?): String {
        if (url == null) {
            return ""
        }
        try {
            val resource = URL(url)
            val host = resource.host
            if (host.isNotEmpty() && url.endsWith(host)) {
                // handle ...example.com
                return ""
            }
        } catch (e: MalformedURLException) {
            return ""
        }
        val startIndex = url.lastIndexOf('/') + 1
        val length = url.length

        // find end index for ?
        var lastQMPos = url.lastIndexOf('?')
        if (lastQMPos == -1) {
            lastQMPos = length
        }

        // find end index for #
        var lastHashPos = url.lastIndexOf('#')
        if (lastHashPos == -1) {
            lastHashPos = length
        }

        // calculate the end index
        val endIndex = lastQMPos.coerceAtMost(lastHashPos)
        return url.substring(startIndex, endIndex)
    }
}