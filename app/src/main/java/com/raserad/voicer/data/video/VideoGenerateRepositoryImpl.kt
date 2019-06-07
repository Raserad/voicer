package com.raserad.voicer.data.video

import com.raserad.voicer.data.sound.entities.SoundRecordObject
import com.raserad.voicer.data.video.entites.ReleaseVideoObject
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.video.generate.VideoGenerateRepository
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo
import io.realm.Realm


class VideoGenerateRepositoryImpl: VideoGenerateRepository {

    override fun generateVideo(project: Project) {


        val realm = Realm.getDefaultInstance()

        val videoObject = realm.where(ReleaseVideoObject::class.java).equalTo("uid", project.uid).findFirst()
        val video = ReleaseVideo(videoObject!!.path)

        val recordObjects = realm.where(SoundRecordObject::class.java).equalTo("uid", project.uid).findAll()

        val records: MutableList<SoundRecord> = ArrayList()
        recordObjects.forEach {recordObject ->
            if(!recordObject.isEnabled) return@forEach
            records.add(SoundRecord(recordObject.id, recordObject.path, recordObject.start, recordObject.end, recordObject.total, recordObject.isEnabled))
        }

        realm.close()
    }
}