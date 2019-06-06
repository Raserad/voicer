package com.raserad.voicer.data.video

import android.util.Log
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.raserad.voicer.App
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
            records.add(SoundRecord(recordObject.id, recordObject.path, recordObject.start, recordObject.end, recordObject.isEnabled))
        }

        realm.close()

        val ffmpeg = FFmpeg.getInstance(App.getContext())

        try {
            val command = "-i ${records[0].path} " +
                    video.path
            if(ffmpeg.isFFmpegCommandRunning) {
                ffmpeg.killRunningProcesses()
            }
            ffmpeg.execute(command.split(" ").toTypedArray(), object: FFmpegExecuteResponseHandler {
                override fun onFinish() {
                    Log.d("VIDEO_GENERATE", "Finish")
                }

                override fun onSuccess(message: String?) {
                    Log.d("VIDEO_GENERATE", message)
                }

                override fun onFailure(message: String?) {
                    Log.d("VIDEO_GENERATE", message)
                }

                override fun onProgress(message: String?) {
                    Log.d("VIDEO_GENERATE", message)
                }

                override fun onStart() {
                    Log.d("VIDEO_GENERATE", "Start")
                }

            })
        } catch (e: Throwable) {
            Log.e("test", "FFMPEG Errors", e)
        }

    }
}