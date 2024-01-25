package com.app.leopoly.common

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.app.leopoly.R

class RoundLinerLayoutNormal : LinearLayout {
    constructor(context: Context?) : super(context) {
        initBackground()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initBackground()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initBackground()
    }

    private fun initBackground() {
        background = ViewUtils.generateBackgroundWithShadow(
            this,
            R.color.colorWhite,
            R.dimen.radius_corner_top_left,
            R.color.colorPrimary_two,
            R.dimen.elevation,
            Gravity.CENTER
        )
    }
}