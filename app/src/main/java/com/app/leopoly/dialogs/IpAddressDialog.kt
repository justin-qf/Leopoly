package com.app.leopoly.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.leopoly.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView

class IpAddressDialog(context: Activity) : Dialog(context) {
    var holder: ViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.ip_address_dialog)
        holder = ViewHolder(window!!.decorView)

        val lp = WindowManager.LayoutParams()
        val manager = context.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
        lp.copyFrom(window!!.attributes)
        val point = Point()
        manager.defaultDisplay.getSize(point)
        lp.width = point.x
        lp.height = point.y
        window!!.attributes = lp
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
    }

    inner class ViewHolder internal constructor(view: View) {

        var editText: TextInputEditText = view.findViewById(R.id.ipEditText)
        var editLayout: TextInputLayout = view.findViewById(R.id.ipAddressLayout)
        var submitBtn: AppCompatButton = view.findViewById(R.id.btnDialogGet)

    }
}