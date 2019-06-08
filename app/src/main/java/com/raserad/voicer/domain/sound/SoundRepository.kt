package com.raserad.voicer.domain.sound

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import io.reactivex.Observable

interface SoundRepository {

    fun getList(project: Project): Observable<MutableList<SoundRecord>>

    fun addToProject(project: Project, soundRecord: SoundRecord): Observable<SoundRecord>

    fun enableInProject(project: Project, soundRecord: SoundRecord, isEnabled: Boolean): Observable<Boolean>
}