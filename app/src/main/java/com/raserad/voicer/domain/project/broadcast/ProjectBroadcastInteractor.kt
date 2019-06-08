package com.raserad.voicer.domain.project.broadcast

import com.raserad.voicer.domain.project.entities.Project

class ProjectBroadcastInteractor(
    private val projectBroadcastRepository: ProjectBroadcastRepository
) {

    fun remember(projct: Project) = projectBroadcastRepository.remember(projct)

    fun getRemembered() = projectBroadcastRepository.getRemembered()
}