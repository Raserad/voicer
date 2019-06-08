package com.raserad.voicer.presentation.ui.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.raserad.voicer.R


class SoundTrackView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0): View(context, attrs, defStyleAttr) {

    private var total: Float = 1f
    private var start: Float = 1f
    private var end: Float = 1f

    private var background: Paint = Paint()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        background.color = resources.getColor(R.color.colorPrimary)

        val left = width.toFloat() / 100 * (start / total * 100)
        val right = width.toFloat() - width.toFloat() / 100 * ((total - end) / total * 100)

        canvas?.drawRect(left, 0f, right, height.toFloat(), background)
    }

    fun initTrackData(total: Long, start: Long, end: Long) {
        this.total = total.toFloat()
        this.start = start.toFloat()
        this.end = end.toFloat()
    }
}