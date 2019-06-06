package com.raserad.voicer.domain.sound

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord

class SoundInteractor(
    private val soundRepository: SoundRepository
) {

    fun getList(project: Project) = soundRepository.getList(project)

    fun addToProject(project: Project, soundRecord: SoundRecord) = soundRepository.addToProject(project, soundRecord)

    fun enableInProject(project: Project, soundRecord: SoundRecord, isEnabled: Boolean) = soundRepository.enableInProject(project, soundRecord, isEnabled)
}