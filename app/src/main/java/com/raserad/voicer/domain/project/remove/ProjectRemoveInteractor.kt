package com.raserad.voicer.domain.project.remove

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.listener.ProjectEventType
import com.raserad.voicer.domain.project.listener.ProjectListenerRepository
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ProjectRemoveInteractor(
    private val projectRemoveRepository: ProjectRemoveRepository
) {

    fun remove(project: Project): Observable<Unit> {
        projectRemoveRepository.removeMarked()
        projectRemoveRepository.markToRemove(project)
        return Observable.timer(REMOVE_DELAY.toLong(), TimeUnit.SECONDS)
            .map {
                projectRemoveRepository.removeMarked()
            }
    }

    fun cancelRemoving() {
        projectRemoveRepository.unmarkMarked()
    }

    fun removeMarked() = projectRemoveRepository.removeMarked()

    companion object {
        private const val REMOVE_DELAY = 5
    }
}