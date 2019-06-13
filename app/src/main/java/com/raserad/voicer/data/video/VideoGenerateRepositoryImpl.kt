package com.raserad.voicer.data.video

import android.util.Log
import com.raserad.voicer.data.sound.entities.SoundRecordObject
import com.raserad.voicer.data.sound.entities.SoundRecordRemoveObject
import com.raserad.voicer.data.sound.entities.SoundRecordRemoveTempObject
import com.raserad.voicer.data.sound.entities.SoundRecordTempObject
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

            val records: MutableList<SoundRecord> = ArrayList()

            val recordObjects = realm.where(SoundRecordObject::class.java).equalTo("uid", project.uid).findAll()

            recordObjects.forEach {recordObject ->
                records.add(SoundRecord(recordObject.id, recordObject.path, recordObject.start, recordObject.end, recordObject.total, recordObject.isEnabled))
            }

            val tempRecordObjects = realm.where(SoundRecordTempObject::class.java).equalTo("uid", project.uid).findAll()

            tempRecordObjects.forEach {recordObject ->
                val duplicate = records.filter { it.id == recordObject.id }
                if(duplicate.isNotEmpty()) {
                    records.remove(duplicate.first())
                }
                records.add(SoundRecord(recordObject.id, recordObject.path, recordObject.start, recordObject.end, recordObject.total, recordObject.isEnabled))
            }

            val removeObjects = realm.where(SoundRecordRemoveObject::class.java).equalTo("uid", project.uid).findAll()

            removeObjects.forEach{removeObject ->
                val remove = records.filter { it.id == removeObject.id }
                if(remove.isNotEmpty()) {
                    records.remove(remove.first())
                }
            }

            val removeTempObjects = realm.where(SoundRecordRemoveTempObject::class.java).equalTo("uid", project.uid).findAll()

            Log.d("COUNTER", "" + removeTempObjects.count())

            removeTempObjects.forEach{removeObject ->
                val remove = records.filter { it.id == removeObject.id }
                if(remove.isNotEmpty()) {
                    records.remove(remove.first())
                }
            }

            realm.close()

            var command = "-y -i ${project.video}.aac"

            records.forEach {record ->
                if(record.isEnabled) {
                    command += " -i ${record.path}"
                }
            }

            command += " -filter_complex "

            var i = 1
            records.forEach { record ->
                if(record.start > 0L && record.isEnabled) {
                    command += "[$i]adelay=${record.start}|${record.start}[${i}a];"
                }
                i++
            }

            command += if(records.any { it.isEnabled }) {
                "[0]volume=0.1[0a];[0a]"
            } else {
                "[0]"
            }

            i = 1
            records.forEach { record ->
                if(record.isEnabled) {
                    command += "[${i}a]"
                }
                i++
            }

            command += "amix=${records.filter { it.isEnabled }.count() + 1}"

            command += " ${project.video}_temp.aac"

            Log.d("RESUL_COMMAND", command)
            FFmpeg.execute(command, {

                val tempAudio = File("${project.video}_temp.aac")
                FFmpeg.execute("-y -i ${source.path} -i ${project.video}_temp.aac -map 0:v:0 -map 1:a:0 -vcodec copy -r 30 -b:v 2100k -acodec aac -strict experimental -b:a 48k -ar 44100 ${tempVideo.path}", {

                    val realm = Realm.getDefaultInstance()

                    realm.executeTransaction {db ->
                        records.forEach { soundRecord ->
                            val recordObject = SoundRecordObject()
                            recordObject.uid = project.uid
                            recordObject.id = soundRecord.id
                            recordObject.path = soundRecord.path
                            recordObject.start = soundRecord.start
                            recordObject.end = soundRecord.end
                            recordObject.total = soundRecord.total
                            recordObject.isEnabled = soundRecord.isEnabled
                            db.copyToRealmOrUpdate(recordObject)
                        }

                        val removeObjects = db.where(SoundRecordRemoveTempObject::class.java).findAll()

                        removeObjects.forEach {remove ->
                            val removeObject = SoundRecordRemoveObject()
                            removeObject.uid = remove.uid
                            removeObject.id = remove.id
                            db.copyToRealmOrUpdate(removeObject)
                        }

                        removeObjects.deleteAllFromRealm()
                    }

                    realm.close()

                    tempVideo.copyTo(source, true)
                    tempVideo.delete()
                    tempAudio.delete()

                    observer.onNext(true)
                }, {
                    Log.d("FFMPEG", it)
                    tempVideo.delete()
                    tempAudio.delete()
                })
            }, {
                Log.d("FFMPEG", it)
                tempVideo.delete()
            })

        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
}