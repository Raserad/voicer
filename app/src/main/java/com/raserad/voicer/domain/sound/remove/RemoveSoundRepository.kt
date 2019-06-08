package com.raserad.voicer.domain.sound.remove

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import io.reactivex.Observable

interface RemoveSoundRepository {

    fun markToRemove(project: Project, soundRecord: SoundRecord): Observable<Boolean>

    fun unmarkMarked(): Observable<Boolean>

    fun removeMarked(): Observable<Boolean>
}