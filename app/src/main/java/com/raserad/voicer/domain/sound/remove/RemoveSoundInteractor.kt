package com.raserad.voicer.domain.sound.remove

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class RemoveSoundInteractor(
    private val removeSoundRepository: RemoveSoundRepository
) {

    fun removeFromProject(project: Project, soundRecord: SoundRecord): Observable<Unit> {
        return removeSoundRepository.removeMarked()
            .flatMap { removeSoundRepository.markToRemove(project, soundRecord) }
            .flatMap { Observable.timer(REMOVE_DELAY.toLong(), TimeUnit.SECONDS) }
            .flatMap { removeSoundRepository.removeMarked() }
            .map {  }
    }

    fun cancelRemoving() = removeSoundRepository.unmarkMarked()

    fun removeMarked() = removeSoundRepository.removeMarked()

    companion object {
        private const val REMOVE_DELAY = 5
    }
}