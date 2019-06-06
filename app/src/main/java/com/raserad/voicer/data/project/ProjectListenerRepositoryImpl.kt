package com.raserad.voicer.data.project

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.listener.ProjectEventType
import com.raserad.voicer.domain.project.listener.ProjectListenerRepository
import com.raserad.voicer.domain.project.listener.entities.ProjectEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ProjectListenerRepositoryImpl: ProjectListenerRepository {

    private val eventPublisher = PublishSubject.create<ProjectEvent>()

    override fun getListener(): Observable<ProjectEvent> {
        return eventPublisher
    }

    override fun notify(type: ProjectEventType, project: Project) {
        eventPublisher.onNext(ProjectEvent(type, project))
    }
}