package com.raserad.voicer.domain.sound.remove

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class RemoveSoundInteractor(
    private val removeSoundRepository: RemoveSoundRepository
) {

    private val REMOVE_DELAY = 5

    fun removeFromProject(project: Project, soundRecord: SoundRecord): Observable<Unit> {
        removeSoundRepository.removeMarked()
        removeSoundRepository.markToRemove(project, soundRecord)
        return Observable.timer(REMOVE_DELAY.toLong(), TimeUnit.SECONDS)
            .map {
                removeSoundRepository.removeMarked()
            }
    }

    fun cancelRemoving() = removeSoundRepository.unmarkMarked()

    fun removeMarked() = removeSoundRepository.removeMarked()
}