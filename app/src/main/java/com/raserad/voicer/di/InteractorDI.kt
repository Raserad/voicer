package com.raserad.voicer.di

import com.raserad.voicer.domain.project.create.ProjectCreateInteractor
import com.raserad.voicer.domain.project.list.ProjectListInteractor
import com.raserad.voicer.domain.project.listener.ProjectListenerInteractor
import com.raserad.voicer.domain.project.remove.ProjectRemoveInteractor
import com.raserad.voicer.domain.project.share.ProjectSharingInteractor
import com.raserad.voicer.domain.project.broadcast.ProjectBroadcastInteractor
import com.raserad.voicer.domain.sound.SoundInteractor
import com.raserad.voicer.domain.sound.record.SoundRecordInteractor
import com.raserad.voicer.domain.sound.remove.RemoveSoundInteractor
import com.raserad.voicer.domain.video.VideoInteractor
import com.raserad.voicer.domain.video.generate.VideoGenerateInteractor
import com.raserad.voicer.domain.video.trim.VideoTrimInteractor

class InteractorDI(private val repositoryDI: RepositoryDI) {

    fun getProjectList() = ProjectListInteractor(repositoryDI.getProjectList())

    fun getProjectRemove() = ProjectRemoveInteractor(
        repositoryDI.getProjectRemove()
    )

    fun getProjectSharing() = ProjectSharingInteractor(repositoryDI.getProjectSharing())

    fun getProjectCreate() = ProjectCreateInteractor(
        repositoryDI.getProjectCreate(),
        repositoryDI.getProjectListener(),
        repositoryDI.getVideoTrim()
    )

    fun getTempProject() =
        ProjectBroadcastInteractor(repositoryDI.getTempProject())

    fun getProjectListener() = ProjectListenerInteractor(repositoryDI.getProjectListener())

    fun getVideo() = VideoInteractor(repositoryDI.getVideo())

    fun getSound() = SoundInteractor(repositoryDI.getSound())

    fun getSoundRecord() = SoundRecordInteractor(repositoryDI.getSoundRecord())

    fun getRemoveSound() = RemoveSoundInteractor(repositoryDI.getRemoveSound())

    fun getVideoGenerate() = VideoGenerateInteractor(repositoryDI.getVideoGenerate())

    fun getVideoTrim() = VideoTrimInteractor(repositoryDI.getVideoTrim())
}