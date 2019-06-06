package com.raserad.voicer.domain.project.share

import com.raserad.voicer.domain.project.entities.Project

class ProjectSharingInteractor(private val projectSharingRepository: ProjectSharingRepository) {

    fun share(project: Project) = projectSharingRepository.share(project)
}