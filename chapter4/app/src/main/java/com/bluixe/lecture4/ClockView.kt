package com.bluixe.lecture4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * TODO: document your custom view class.
 */
class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val paint = Paint(Paint.ANTI_ALIAS_FLAG) //抗锯齿
    init {
        val tickHandler : Handler = Handler(Looper.getMainLooper())
        val runnable : Runnable = object: Runnable {
            override fun run() {
                postInvalidate()

                tickHandler.postDelayed(this, 1000)
            }
        }
        tickHandler.post(runnable)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val cal =  Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)
        val hourRotate = (hour * 30.0f + minute / 60.0f * 30.0f + second / 3600.0f * 30.0f) * Math.PI / 180
        val minuteRotate = (minute * 6.0f + second / 60.0f * 6.0f) * Math.PI / 180
        val secondRotate = second * 6 * Math.PI / 180


//        canvas?.drawLine(0f, 0f, width.toFloat(), height.toFloat(), paint)
        var centerX = measuredWidth.toFloat()/2
        var centerY = measuredHeight.toFloat()/2
        paint.color = Color.WHITE
        paint.strokeWidth = 10f
        for (i in 1..12) {
            canvas?.drawLine(centerX, 0f, centerX, 0.1f*centerY, paint)
            canvas?.rotate(30f, centerX, centerY)
        }
        for (i in 1..60) {
            paint.alpha = 80
            canvas?.drawLine(centerX, 0f, centerX, 0.08f*centerY, paint)
            canvas?.rotate(6f, centerX, centerY)
        }

        paint.color = Color.GRAY
        paint.strokeWidth = 30f
        canvas?.drawLine(centerX*(1-0.05*sin(hourRotate)).toFloat(), centerY*(1+0.05*cos(hourRotate)).toFloat(),
            centerX*(1+0.6*sin(hourRotate)).toFloat(),centerY*(1-0.6*cos(hourRotate)).toFloat(), paint)
        paint.color = Color.WHITE
        paint.strokeWidth = 20f
        canvas?.drawLine(centerX*(1-0.05*sin(minuteRotate)).toFloat(), centerY*(1+0.05*cos(minuteRotate)).toFloat(),
            centerX*(1+0.8*sin(minuteRotate)).toFloat(),centerY*(1-0.8*cos(minuteRotate)).toFloat(), paint)
        paint.color = Color.RED
        paint.strokeWidth = 10f
        canvas?.drawLine(centerX*(1-0.05*sin(secondRotate)).toFloat(), centerY*(1+0.05*cos(secondRotate)).toFloat(),
            centerX*(1+sin(secondRotate)).toFloat(), centerY*(1-cos(secondRotate)).toFloat(), paint)



    }

}