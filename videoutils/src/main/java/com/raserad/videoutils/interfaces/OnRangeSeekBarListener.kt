package com.raserad.videoutils.interfaces

import com.raserad.videoutils.view.RangeSeekBarView

/**
 * Created by Deep Patel
 * (Sr. Android Developer)
 * on 6/4/2018
 */
interface OnRangeSeekBarListener {
    fun onCreate(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float)

    fun onSeek(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float)

    fun onSeekStart(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float)

    fun onSeekStop(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float)
}
