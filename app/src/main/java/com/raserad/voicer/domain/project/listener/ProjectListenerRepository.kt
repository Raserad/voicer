package com.raserad.voicer.domain.project.listener

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.listener.entities.ProjectEvent
import io.reactivex.Observable

interface ProjectListenerRepository {

    fun getListener(): Observable<ProjectEvent>

    fun notify(type: ProjectEventType, project: Project)
}