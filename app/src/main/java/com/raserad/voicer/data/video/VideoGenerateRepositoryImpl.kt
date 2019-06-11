package com.raserad.voicer.data.video

import android.util.Log
import com.raserad.voicer.data.sound.entities.SoundRecordObject
import com.raserad.voicer.data.utils.FFmpeg
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.video.generate.VideoGenerateRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.io.File

class VideoGenerateRepositoryImpl: VideoGenerateRepository {

    override fun generateVideo(project: Project): Observable<Boolean> {
        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()

            val source = File(project.video)
            val tempVideo = File(project.video + "_temp.mp4")

            source.copyTo(tempVideo, true)

            val recordObjects = realm.where(SoundRecordObject::class.java).equalTo("uid", project.uid).findAll()

            val records: MutableList<SoundRecord> = ArrayList()
            recordObjects.forEach {recordObject ->
                if(!recordObject.isEnabled) return@forEach
                records.add(SoundRecord(recordObject.id, recordObject.path, recordObject.start, recordObject.end, recordObject.total, recordObject.isEnabled))
            }

            realm.close()

            var command = "-y -i ${project.video}.aac"

            records.forEach {record ->
                command += " -i ${record.path}"
            }

//            command += " -filter_complex amix=inputs=${records.count() + 1}:duration=first:dropout_transition=${records.count() + 1};[1]adelay=5|5 ${project.video}_temp.aac"

            command += " -filter_complex "

            var i = 1
            records.forEach { record ->
                if(record.start == 0L) {
                    i++
                    return@forEach
                }
                command += "[$i]adelay=${record.start}|${record.start}[${i}a];"
                i++
            }

            command += "[0]"
            i = 1
            records.forEach { record ->
                command += "[${i}a]"
                i++
            }

            command += "amix=${records.count() + 1}"

            command += " ${project.video}_temp.aac"

            Log.d("RESUL_COMMAND", command)
            FFmpeg.execute(command, {
                FFmpeg.execute("-y -i ${tempVideo.path} -i ${project.video}_temp.aac -map 0:v:0 -map 1:a:0 -vcodec copy -r 30 -b:v 2100k -acodec aac -strict experimental -b:a 48k -ar 44100 ${source.path}", {
                    observer.onNext(true)
                    tempVideo.delete()
                }, {
                    Log.d("FFMPEG", it)
                    tempVideo.delete()
                })
            }, {
                Log.d("FFMPEG", it)
                tempVideo.delete()
            })

        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
    }
}