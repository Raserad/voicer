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

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.raserad.videotrimming.R
import java.util.Vector

/**
 * Created by Deep Patel
 * (Sr. Android Developer)
 * on 6/4/2018
 */
class Thumb private constructor() {

    var index: Int = 0
        private set
    var value: Float = 0.toFloat()
    var pos: Float = 0.toFloat()
    var bitmap: Bitmap? = null
        private set(bitmap) {
            field = bitmap
            widthBitmap = bitmap!!.width
            heightBitmap = bitmap.height
        }
    var widthBitmap: Int = 0
        private set
    private var heightBitmap: Int = 0

    var lastTouchX: Float = 0.toFloat()

    init {
        value = 0f
        pos = 0f
    }

    companion object {

        fun initThumbs(resources: Resources): List<Thumb> {

            val thumbs = Vector<Thumb>()

            for (i in 0..1) {
                val th = Thumb()
                th.index = i
                if (i == 0) {
                    val resImageLeft = R.drawable.trimmer_slider
                    th.bitmap = BitmapFactory.decodeResource(resources, resImageLeft)
                } else {
                    val resImageRight = R.drawable.trimmer_slider
                    th.bitmap = BitmapFactory.decodeResource(resources, resImageRight)
                }

                thumbs.add(th)
            }

            return thumbs
        }

        fun getWidthBitmap(thumbs: List<Thumb>): Int {
            return thumbs[0].widthBitmap
        }

        fun getHeightBitmap(thumbs: List<Thumb>): Int {
            return thumbs[0].heightBitmap
        }
    }
}
