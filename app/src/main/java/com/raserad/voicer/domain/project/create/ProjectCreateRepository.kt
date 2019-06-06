package com.raserad.voicer.domain.project.create

import com.raserad.voicer.domain.project.entities.Project

interface ProjectCreateRepository {

    fun create(project: Project)
}