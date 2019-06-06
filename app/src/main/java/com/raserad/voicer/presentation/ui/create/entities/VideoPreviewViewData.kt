package com.raserad.voicer.presentation.ui.create.entities

import android.graphics.Bitmap

data class VideoPreviewViewData(
    var path: String,
    var isSelected: Boolean,

    var preview: Bitmap?
)