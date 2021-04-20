package com.moshaoxia.viewinspect.hookview

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View

/**
 * @author moshaoxia
 * Date：2021/4/19
 * Email:345079386@qq.com
 * Description: 描边背景
 */
class ViewBorderDrawable(view: View) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect: Rect = Rect(0, 0, view.width, view.height)
    override fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 10f
        paint.pathEffect = DashPathEffect(floatArrayOf(8f, 4f), 0f)
    }
}