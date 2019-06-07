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
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.raserad.videoutils.interfaces.OnRangeSeekBarListener
import com.raserad.videotrimming.R

import java.util.ArrayList

/**
 * Created by Deep Patel
 * (Sr. Android Developer)
 * on 6/4/2018
 */
class RangeSeekBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private var mHeightTimeLine: Int = 0
    var thumbs: List<Thumb>? = null
        private set
    private var mListeners: MutableList<OnRangeSeekBarListener>? = null
    private var mThumbWidth: Float = 0.toFloat()
    private var mThumbHeight: Float = 0.toFloat()
    private var mViewWidth: Int = 0
    private var mPixelRangeMin: Float = 0.toFloat()
    private var mPixelRangeMax: Float = 0.toFloat()
    private var mScaleRangeMax: Float = 0.toFloat()
    private var mFirstRun: Boolean = false

    private val mShadow = Paint()
    private val mLine = Paint()

    private var currentThumb = 0

    init {
        init()
    }

    private fun init() {
        thumbs = Thumb.initThumbs(resources)
        mThumbWidth = Thumb.getWidthBitmap(thumbs!!).toFloat()
        mThumbHeight = Thumb.getHeightBitmap(thumbs!!).toFloat()

        mScaleRangeMax = 100f
        mHeightTimeLine = context.resources.getDimensionPixelOffset(R.dimen.frames_video_height)

        isFocusable = true
        isFocusableInTouchMode = true

        mFirstRun = true

        val shadowColor = ContextCompat.getColor(context, R.color.shadow_color)
        mShadow.isAntiAlias = true
        mShadow.color = shadowColor
        mShadow.alpha = 177

        val lineColor = ContextCompat.getColor(context, R.color.line_color)
        mLine.isAntiAlias = true
        mLine.color = lineColor
        mLine.alpha = 200
    }

    fun initMaxWidth() {
        onSeekStop(this, 0, thumbs!![0].value)
        onSeekStop(this, 1, thumbs!![1].value)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val minW = paddingLeft + paddingRight + suggestedMinimumWidth
        mViewWidth = View.resolveSizeAndState(minW, widthMeasureSpec, 1)

        val minH = paddingBottom + paddingTop + mThumbHeight.toInt() + mHeightTimeLine
        val viewHeight = View.resolveSizeAndState(minH, heightMeasureSpec, 1)

        setMeasuredDimension(mViewWidth, viewHeight)

        mPixelRangeMin = 0f
        mPixelRangeMax = mViewWidth - mThumbWidth

        if (mFirstRun) {
            for (i in thumbs!!.indices) {
                val th = thumbs!![i]
                th.value = mScaleRangeMax * i
                th.pos = mPixelRangeMax * i
            }
            onCreate(this, currentThumb, getThumbValue(currentThumb))
            mFirstRun = false
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawShadow(canvas)
        drawThumbs(canvas)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val mThumb: Thumb
        val mThumb2: Thumb
        val coordinate = ev.x
        val action = ev.action

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                /*Remember where we started*/
                currentThumb = getClosestThumb(coordinate)

                if (currentThumb == -1) {
                    return false
                }

                mThumb = thumbs!![currentThumb]
                mThumb.lastTouchX = coordinate
                onSeekStart(this, currentThumb, mThumb.value)
                return true
            }
            MotionEvent.ACTION_UP -> {

                if (currentThumb == -1) {
                    return false
                }

                mThumb = thumbs!![currentThumb]
                onSeekStop(this, currentThumb, mThumb.value)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                mThumb = thumbs!![currentThumb]
                mThumb2 = thumbs!![if (currentThumb == 0) 1 else 0]
                /* Calculate the distance moved*/
                val dx = coordinate - mThumb.lastTouchX
                val newX = mThumb.pos + dx
                if (currentThumb == 0) {

                    if (newX + mThumb.widthBitmap >= mThumb2.pos) {
                        mThumb.pos = mThumb2.pos - mThumb.widthBitmap
                    } else if (newX <= mPixelRangeMin) {
                        mThumb.pos = mPixelRangeMin
                    } else {
                        /*Check if thumb is not out of max width*/
                        checkPositionThumb(mThumb, mThumb2, dx, true)
                        /* Move the object*/
                        mThumb.pos = mThumb.pos + dx

                        /* Remember this touch position for the next move event*/
                        mThumb.lastTouchX = coordinate
                    }

                } else {
                    if (newX <= mThumb2.pos + mThumb2.widthBitmap) {
                        mThumb.pos = mThumb2.pos + mThumb.widthBitmap
                    } else if (newX >= mPixelRangeMax) {
                        mThumb.pos = mPixelRangeMax
                    } else {
                        /*Check if thumb is not out of max width*/
                        checkPositionThumb(mThumb2, mThumb, dx, false)
                        /* Move the object*/
                        mThumb.pos = mThumb.pos + dx
                        /* Remember this touch position for the next move event*/
                        mThumb.lastTouchX = coordinate
                    }
                }

                setThumbPos(currentThumb, mThumb.pos)

                invalidate()
                return true
            }
        }
        return false
    }

    private fun checkPositionThumb(mThumbLeft: Thumb, mThumbRight: Thumb, dx: Float, isLeftMove: Boolean) {

    }

    private fun getUnstuckFrom(index: Int): Int {
        val unstuck = 0
        val lastVal = thumbs!![index].value
        for (i in index - 1 downTo 0) {
            val th = thumbs!![i]
            if (th.value != lastVal)
                return i + 1
        }
        return unstuck
    }

    private fun pixelToScale(index: Int, pixelValue: Float): Float {
        val scale = pixelValue * 100 / mPixelRangeMax
        if (index == 0) {
            val pxThumb = scale * mThumbWidth / 100
            return scale + pxThumb * 100 / mPixelRangeMax
        } else {
            val pxThumb = (100 - scale) * mThumbWidth / 100
            return scale - pxThumb * 100 / mPixelRangeMax
        }
    }

    private fun scaleToPixel(index: Int, scaleValue: Float): Float {
        val px = scaleValue * mPixelRangeMax / 100
        if (index == 0) {
            val pxThumb = scaleValue * mThumbWidth / 100
            return px - pxThumb
        } else {
            val pxThumb = (100 - scaleValue) * mThumbWidth / 100
            return px + pxThumb
        }
    }

    private fun calculateThumbValue(index: Int) {
        if (index < thumbs!!.size && !thumbs!!.isEmpty()) {
            val th = thumbs!![index]
            th.value = pixelToScale(index, th.pos)
            onSeek(this, index, th.value)
        }
    }

    private fun calculateThumbPos(index: Int) {
        if (index < thumbs!!.size && !thumbs!!.isEmpty()) {
            val th = thumbs!![index]
            th.pos = scaleToPixel(index, th.value)
        }
    }

    private fun getThumbValue(index: Int): Float {
        return thumbs!![index].value
    }

    fun setThumbValue(index: Int, value: Float) {
        thumbs!![index].value = value
        calculateThumbPos(index)
        invalidate()
    }

    private fun setThumbPos(index: Int, pos: Float) {
        thumbs!![index].pos = pos
        calculateThumbValue(index)
        invalidate()
    }

    private fun getClosestThumb(coordinate: Float): Int {
        var closest = -1
        if (!thumbs!!.isEmpty()) {
            for (i in thumbs!!.indices) {
                val tcoordinate = thumbs!![i].pos + mThumbWidth
                if (coordinate >= thumbs!![i].pos && coordinate <= tcoordinate) {
                    closest = thumbs!![i].index
                }
            }
        }
        return closest
    }

    private fun drawShadow(canvas: Canvas) {
        if (!thumbs!!.isEmpty()) {

            for (th in thumbs!!) {
                if (th.index == 0) {
                    val x = th.pos + paddingLeft
                    if (x > mPixelRangeMin) {
                        val mRect = Rect(mThumbWidth.toInt(), 0, (x + mThumbWidth).toInt(), mHeightTimeLine)
                        canvas.drawRect(mRect, mShadow)
                    }
                } else {
                    val x = th.pos - paddingRight
                    if (x < mPixelRangeMax) {
                        val mRect = Rect(x.toInt(), 0, (mViewWidth - mThumbWidth).toInt(), mHeightTimeLine)
                        canvas.drawRect(mRect, mShadow)
                    }
                }
            }
        }
    }

    private fun drawThumbs(canvas: Canvas) {

        if (!thumbs!!.isEmpty()) {
            for (th in thumbs!!) {
                if (th.index == 0) {
                    canvas.drawBitmap(th.bitmap, th.pos + paddingLeft, 0f, null)
                } else {
                    canvas.drawBitmap(th.bitmap, th.pos - paddingRight, 0f, null)
                }
            }
        }
    }

    fun addOnRangeSeekBarListener(listener: OnRangeSeekBarListener) {

        if (mListeners == null) {
            mListeners = ArrayList()
        }

        mListeners!!.add(listener)
    }

    private fun onCreate(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        if (mListeners == null)
            return

        for (item in mListeners!!) {
            item.onCreate(rangeSeekBarView, index, value)
        }
    }

    private fun onSeek(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        if (mListeners == null)
            return

        for (item in mListeners!!) {
            item.onSeek(rangeSeekBarView, index, value)
        }
    }

    private fun onSeekStart(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        if (mListeners == null)
            return

        for (item in mListeners!!) {
            item.onSeekStart(rangeSeekBarView, index, value)
        }
    }

    private fun onSeekStop(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        if (mListeners == null)
            return

        for (item in mListeners!!) {
            item.onSeekStop(rangeSeekBarView, index, value)
        }
    }

    companion object {

        private val TAG = RangeSeekBarView::class.java.simpleName
    }
}
