package com.raserad.voicer.domain.video.generate

import com.raserad.voicer.domain.project.entities.Project

interface VideoGenerateRepository {

    fun generateVideo(project: Project)
}