package com.raserad.voicer.di

import com.raserad.voicer.data.project.*
import com.raserad.voicer.data.sound.RemoveSoundRepositoryImpl
import com.raserad.voicer.data.sound.SoundRecordRepositoryImpl
import com.raserad.voicer.data.sound.SoundRepositoryImpl
import com.raserad.voicer.data.video.ReleaseVideoRepositoryImpl
import com.raserad.voicer.data.video.VideoGenerateRepositoryImpl
import com.raserad.voicer.data.video.VideoRepositoryImpl
import com.raserad.voicer.domain.project.create.ProjectCreateRepository
import com.raserad.voicer.domain.project.list.ProjectListRepository
import com.raserad.voicer.domain.project.listener.ProjectListenerRepository
import com.raserad.voicer.domain.project.remove.ProjectRemoveRepository
import com.raserad.voicer.domain.project.share.ProjectSharingRepository
import com.raserad.voicer.domain.sound.SoundRepository
import com.raserad.voicer.domain.sound.record.SoundRecordRepository
import com.raserad.voicer.domain.sound.remove.RemoveSoundRepository
import com.raserad.voicer.domain.video.VideoRepository
import com.raserad.voicer.domain.video.release.ReleaseVideoRepository

class RepositoryDI {

    fun getProjectList() = ProjectListRepositoryImpl()

    fun getProjectRemove() = ProjectRemoveRepositoryImpl()

    fun getProjectSharing() = ProjectSharingRepositoryImpl()

    fun getVideo() = VideoRepositoryImpl()

    fun getProjectCreate() = ProjectCreateRepositoryImpl()

    private var projectListenerRepository: ProjectListenerRepository? = null
    fun getProjectListener(): ProjectListenerRepository {
        if(projectListenerRepository == null) {
            projectListenerRepository = ProjectListenerRepositoryImpl()
        }
        return projectListenerRepository!!
    }

    fun getSound() = SoundRepositoryImpl()

    fun getSoundRecord() = SoundRecordRepositoryImpl()

    fun getRemoveSound() = RemoveSoundRepositoryImpl()

    fun getReleaseVideo() = ReleaseVideoRepositoryImpl()

    fun getVideoGenerate() = VideoGenerateRepositoryImpl()
}