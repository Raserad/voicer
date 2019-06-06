/*
 * MIT License
 *
 * Copyright (c) 2016 Knowledge, education for life.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.raserad.videoutils.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.raserad.videoutils.interfaces.OnProgressVideoListener
import com.raserad.videoutils.interfaces.OnRangeSeekBarListener
import com.raserad.videotrimming.R

/**
 * Created by Deep Patel
 * (Sr. Android Developer)
 * on 6/4/2018
 */

class ProgressBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), OnRangeSeekBarListener, OnProgressVideoListener {

    private var mProgressHeight: Int = 0
    private var mViewWidth: Int = 0

    private val mBackgroundColor = Paint()
    private val mProgressColor = Paint()

    private var mBackgroundRect: Rect? = null
    private var mProgressRect: Rect? = null

    init {
        init()
    }

    private fun init() {
        val lineProgress = ContextCompat.getColor(context, R.color.progress_color)
        val lineBackground = ContextCompat.getColor(context, R.color.background_progress_color)

        mProgressHeight = context.resources.getDimensionPixelOffset(R.dimen.progress_video_line_height)

        mBackgroundColor.isAntiAlias = true
        mBackgroundColor.color = lineBackground

        mProgressColor.isAntiAlias = true
        mProgressColor.color = lineProgress
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val minW = paddingLeft + paddingRight + suggestedMinimumWidth
        mViewWidth = View.resolveSizeAndState(minW, widthMeasureSpec, 1)

        val minH = paddingBottom + paddingTop + mProgressHeight
        val viewHeight = View.resolveSizeAndState(minH, heightMeasureSpec, 1)

        setMeasuredDimension(mViewWidth, viewHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawLineBackground(canvas)
        drawLineProgress(canvas)
    }

    private fun drawLineBackground(canvas: Canvas) {
        if (mBackgroundRect != null) {
            canvas.drawRect(mBackgroundRect!!, mBackgroundColor)
        }
    }

    private fun drawLineProgress(canvas: Canvas) {
        if (mProgressRect != null) {
            canvas.drawRect(mProgressRect!!, mProgressColor)
        }
    }

    override fun onCreate(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        updateBackgroundRect(index, value)
    }

    override fun onSeek(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        updateBackgroundRect(index, value)
    }

    override fun onSeekStart(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        updateBackgroundRect(index, value)
    }

    override fun onSeekStop(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        updateBackgroundRect(index, value)
    }

    private fun updateBackgroundRect(index: Int, value: Float) {

        if (mBackgroundRect == null) {
            mBackgroundRect = Rect(0, 0, mViewWidth, mProgressHeight)
        }

        val newValue = (mViewWidth * value / 100).toInt()
        if (index == 0) {
            mBackgroundRect = Rect(newValue, mBackgroundRect!!.top, mBackgroundRect!!.right, mBackgroundRect!!.bottom)
        } else {
            mBackgroundRect = Rect(mBackgroundRect!!.left, mBackgroundRect!!.top, newValue, mBackgroundRect!!.bottom)
        }

        updateProgress(0, 0, 0.0f)
    }

    override fun updateProgress(time: Long, max: Long, scale: Float) {
        mProgressRect = if (scale == 0f) {
            Rect(0, mBackgroundRect!!.top, 0, mBackgroundRect!!.bottom)
        } else {
            val newValue = (mViewWidth * scale / 100).toInt()
            Rect(mBackgroundRect!!.left, mBackgroundRect!!.top, newValue, mBackgroundRect!!.bottom)
        }

        invalidate()
    }
}
