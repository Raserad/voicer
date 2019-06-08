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

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AndroidException
import android.util.AttributeSet
import android.util.LongSparseArray
import android.view.View
import com.raserad.videotrimming.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Deep Patel
 * (Sr. Android Developer)
 * on 6/4/2018
 */

class TimeLineView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private var mVideoUri: Uri? = null
    private var mHeightView: Int = 0
    private var mBitmapList: MutableList<Bitmap> = ArrayList()

    private var currentSize = 0

    private var bitmapThread: Disposable? = null

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
        bitmapThread?.dispose()

        bitmapThread = Observable.create<Bitmap> {observer ->
            if (mVideoUri == null) {
                return@create
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

                observer.onNext(bitmap)
            }
            mediaMetadataRetriever.release()
        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext {bitmap ->
            mBitmapList.add(bitmap)
            invalidate()
        }
        .subscribe()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        var x = 0

        for (i in 0 until mBitmapList.count()) {
            val bitmap = mBitmapList[i]

            canvas.drawBitmap(bitmap, x.toFloat(), 0f, null)
            x += bitmap.width
        }
    }

    fun setVideo(data: Uri) {
        mVideoUri = data
        mBitmapList = ArrayList()
        invalidate()
        getBitmap(currentSize)
    }
}
