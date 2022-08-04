package com.tuya.smart.bizubundle.panel.demo.videolock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Create by blitzfeng on 7/14/22
 */
class CircleView :View {

    private val paint = Paint()
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

}