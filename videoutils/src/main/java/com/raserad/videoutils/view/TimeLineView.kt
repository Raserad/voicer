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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AttributeSet
import android.util.LongSparseArray
import android.view.View
import com.raserad.videoutils.utils.BackgroundExecutor
import com.raserad.videoutils.utils.UiThreadExecutor
import com.raserad.videotrimming.R

/**
 * Created by Deep Patel
 * (Sr. Android Developer)
 * on 6/4/2018
 */

class TimeLineView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private var mVideoUri: Uri? = null
    private var mHeightView: Int = 0
    private var mBitmapList: LongSparseArray<Bitmap>? = null

    private var currentSize = 0

    init {
        init()
    }

    private fun init() {
        mHeightView = context.resources.getDimensionPixelOffset(R.dimen.frames_video_height)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW = paddingLeft + paddingRight + suggestedMinimumWidth
        val w = resolveSizeAndState(minW, widthMeasureSpec, 1)

        val minH = paddingBottom + paddingTop + mHeightView
        val h = resolveSizeAndState(minH, heightMeasureSpec, 1)

        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        if (w != oldW) {
            currentSize = w

            getBitmap(currentSize)
        }
    }

    private fun getBitmap(viewWidth: Int) {
        BackgroundExecutor.execute(object : BackgroundExecutor.Task("", 0L, "") {
            override fun execute() {
                try {
                    val thumbnailList = LongSparseArray<Bitmap>()

                    if (mVideoUri == null) {
                        return
                    }

                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    mediaMetadataRetriever.setDataSource(context, mVideoUri)

                    /* Retrieve media data*/
                    val videoLengthInMs =
                        (Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000).toLong()

                    /*Set thumbnail properties (Thumbs are squares)*/
                    val thumbWidth = mHeightView
                    val thumbHeight = mHeightView

                    var numThumbs = Math.ceil((viewWidth.toFloat() / thumbWidth).toDouble()).toInt()

                    if (numThumbs == 0) {
                        numThumbs = 1
                    }

                    val interval = videoLengthInMs / numThumbs

                    for (i in 0 until numThumbs) {
                        var bitmap = mediaMetadataRetriever.getFrameAtTime(
                            i * interval,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )
                        try {
                            bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        thumbnailList.put(i.toLong(), bitmap)
                    }

                    mediaMetadataRetriever.release()
                    returnBitmaps(thumbnailList)
                } catch (e: Throwable) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e)
                }

            }
        }
        )
    }

    private fun returnBitmaps(thumbnailList: LongSparseArray<Bitmap>) {
        UiThreadExecutor.runTask("", {
            mBitmapList = thumbnailList
            invalidate()
        }, 0L)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mBitmapList != null) {
            canvas.save()
            var x = 0

            for (i in 0 until mBitmapList!!.size()) {
                val bitmap = mBitmapList!!.get(i.toLong())

                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, x.toFloat(), 0f, null)
                    x += bitmap.width
                }
            }
        }
    }

    fun setVideo(data: Uri) {
        mVideoUri = data
        mBitmapList = null
        invalidate()
        getBitmap(currentSize)
    }
}
