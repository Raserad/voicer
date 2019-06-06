package com.raserad.voicer.presentation.ui.list.entities

import android.graphics.Bitmap

data class ProjectViewData(
    var title: String,
    var description: String,

    var video: String,

    var preview: Bitmap?
)