package com.raserad.voicer.domain.project.share

import com.raserad.voicer.domain.project.entities.Project

interface ProjectSharingRepository {

    fun share(project: Project)
}