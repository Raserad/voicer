package com.raserad.voicer.domain.project.create.entities

import com.raserad.voicer.domain.video.entities.VideoTrimData

data class ProjectCreateData(
    var title: String,
    var description: String,

    var videoTrimData: VideoTrimData
)