package com.raserad.videoutils.interfaces

import android.net.Uri

/**
 * Created by Deep Patel
 * (Sr. Android Developer)
 * on 6/4/2018
 */
interface OnTrimVideoListener {

    fun getResult(uri: Uri)

    fun cancelAction()
}
