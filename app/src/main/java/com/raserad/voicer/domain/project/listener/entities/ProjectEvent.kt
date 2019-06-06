package com.raserad.voicer.domain.project.listener.entities

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.listener.ProjectEventType

data class ProjectEvent(
    var type: ProjectEventType,
    var project: Project
)