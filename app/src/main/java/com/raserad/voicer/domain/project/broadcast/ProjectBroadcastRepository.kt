package com.raserad.voicer.domain.project.broadcast

import com.raserad.voicer.domain.project.entities.Project

interface ProjectBroadcastRepository {

    fun remember(project: Project)

    fun getRemembered(): Project?
}