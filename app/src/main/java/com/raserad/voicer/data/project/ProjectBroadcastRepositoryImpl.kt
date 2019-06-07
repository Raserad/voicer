package com.raserad.voicer.data.project

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.broadcast.ProjectBroadcastRepository

class ProjectBroadcastRepositoryImpl: ProjectBroadcastRepository {

    private var project: Project? = null

    override fun remember(project: Project) {
        this.project = project
    }

    override fun getRemembered(): Project? {
        val project = this.project
        this.project = null
        return project
    }
}