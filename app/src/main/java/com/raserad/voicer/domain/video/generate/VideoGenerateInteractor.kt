package com.raserad.voicer.domain.video.generate

import com.raserad.voicer.domain.project.entities.Project

class VideoGenerateInteractor(
    private val videoGenerateRepository: VideoGenerateRepository
) {

    fun generateVideo(project: Project) = videoGenerateRepository.generateVideo(project)
}