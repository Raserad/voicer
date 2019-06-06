package com.raserad.voicer.domain.sound.remove

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord

interface RemoveSoundRepository {

    fun markToRemove(project: Project, soundRecord: SoundRecord)

    fun unmarkMarked()

    fun removeMarked()
}