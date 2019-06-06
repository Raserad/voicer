package com.raserad.voicer.domain.project.create.entities

data class ProjectCreateData(
    var title: String,
    var description: String,

    var videoPath: String,
    var videoStartTime: Int,
    var videoEndTime: Int
)