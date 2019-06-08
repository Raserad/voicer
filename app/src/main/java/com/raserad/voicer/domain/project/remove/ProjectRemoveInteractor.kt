package com.raserad.voicer.domain.project.remove

import com.raserad.voicer.domain.project.entities.Project
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ProjectRemoveInteractor(
    private val projectRemoveRepository: ProjectRemoveRepository
) {

    fun remove(project: Project): Observable<Unit> {
        return  projectRemoveRepository.removeMarked()
            .flatMap { projectRemoveRepository.markToRemove(project) }
            .flatMap { Observable.timer(REMOVE_DELAY.toLong(), TimeUnit.SECONDS) }
            .flatMap { projectRemoveRepository.removeMarked() }
            .map {}
    }

    fun cancelRemoving() = projectRemoveRepository.unmarkMarked()

    fun removeMarked() = projectRemoveRepository.removeMarked()

    companion object {
        private const val REMOVE_DELAY = 5
    }
}