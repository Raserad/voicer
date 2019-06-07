package com.raserad.voicer.di

import com.raserad.voicer.data.project.*
import com.raserad.voicer.data.sound.RemoveSoundRepositoryImpl
import com.raserad.voicer.data.sound.SoundRecordRepositoryImpl
import com.raserad.voicer.data.sound.SoundRepositoryImpl
import com.raserad.voicer.data.video.ReleaseVideoRepositoryImpl
import com.raserad.voicer.data.video.VideoGenerateRepositoryImpl
import com.raserad.voicer.data.video.VideoRepositoryImpl
import com.raserad.voicer.data.video.VideoTrimRepositoryImpl
import com.raserad.voicer.domain.project.listener.ProjectListenerRepository
import com.raserad.voicer.domain.project.broadcast.ProjectBroadcastRepository
import com.raserad.voicer.domain.video.trim.VideoTrimRepository

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

    private var projectBroadcastRepository: ProjectBroadcastRepository? = null
    fun getTempProject(): ProjectBroadcastRepository {
        if(projectBroadcastRepository == null) {
            projectBroadcastRepository = ProjectBroadcastRepositoryImpl()
        }
        return projectBroadcastRepository!!
    }

    fun getSound() = SoundRepositoryImpl()

    fun getSoundRecord() = SoundRecordRepositoryImpl()

    fun getRemoveSound() = RemoveSoundRepositoryImpl()

    fun getReleaseVideo() = ReleaseVideoRepositoryImpl()

    fun getVideoGenerate() = VideoGenerateRepositoryImpl()

    private var videoTrimRepository: VideoTrimRepository? = null
    fun getVideoTrim(): VideoTrimRepository {
        if(videoTrimRepository == null) {
            videoTrimRepository = VideoTrimRepositoryImpl()
        }
        return videoTrimRepository!!
    }
}