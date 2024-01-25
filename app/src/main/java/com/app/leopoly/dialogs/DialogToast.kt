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
import com.google.android.material.textview.MaterialTextView

class DialogToast(context: Activity) : Dialog(context) {
    var holder: ViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_toast)
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
        var tvTitle: MaterialTextView = view.findViewById(R.id.tvDialogTitle)
        var tvMessage: MaterialTextView = view.findViewById(R.id.tvDialogMessage)
        var noConnectionImage: ImageView = view.findViewById(R.id.noConnectionImg)
        var closeBtn: AppCompatButton = view.findViewById(R.id.closeBtn)
        var okBtn: AppCompatButton = view.findViewById(R.id.btnDialogOk)
        var messageLayout: LinearLayoutCompat = view.findViewById(R.id.messageLayout)
        var btnDialogLogout: AppCompatButton = view.findViewById(R.id.btnDialogLogout)
        var btnDialogCancel: AppCompatButton = view.findViewById(R.id.btnDialogCancel)
        var btnDialogGet: AppCompatButton = view.findViewById(R.id.btnDialogGet)
        var bottomBtnLayout: ConstraintLayout = view.findViewById(R.id.bottomBtnLayout)
        var userLogoutLayout: LinearLayout = view.findViewById(R.id.userLogoutLayout)
        var userId: MaterialTextView = view.findViewById(R.id.userId)
        var cancelDialog: ImageView = view.findViewById(R.id.cancelDialog)
    }
}