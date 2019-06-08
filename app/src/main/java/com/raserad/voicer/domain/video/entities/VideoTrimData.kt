package com.raserad.voicer.domain.video.entities

data class VideoTrimData(
    var path: String,
    var startTime: Long,
    var endTime: Long
)