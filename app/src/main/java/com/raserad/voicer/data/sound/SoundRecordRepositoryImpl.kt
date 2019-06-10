package com.raserad.voicer.data.sound

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import com.raserad.voicer.App
import com.raserad.voicer.data.utils.FFmpeg
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.sound.record.SoundRecordRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.File


class SoundRecordRepositoryImpl: SoundRecordRepository {

    private val recordingListener = PublishSubject.create<SoundRecord>()

    private var mediaRecorder:  MediaRecorder? = null

    private var currentSoundRecord: SoundRecord? = null

    override fun startRecording(id: Int, time: Long) {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setAudioSamplingRate(16000)

        val projectsDirectory = File(App.getContext()!!.filesDir.path + "/projects")
        projectsDirectory.mkdir()

        val soundPath = projectsDirectory.path + "/" + System.currentTimeMillis() + ".aac"

        currentSoundRecord = SoundRecord(id, soundPath, time, 0, 0, true)

        mediaRecorder?.setOutputFile(soundPath)

        mediaRecorder?.prepare()
        mediaRecorder?.start()
    }

    override fun stopRecording(time: Long, totalTime: Long) {
        if(mediaRecorder == null) {
            return
        }
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            currentSoundRecord?.end = time
            currentSoundRecord?.total = totalTime
<<<<<<< HEAD

//            addRecordOffset(currentSoundRecord!!)

=======
>>>>>>> 55fdb3127cd560e4470c52322e1e45455a9530b7
        } catch (stopException: RuntimeException) {
            val soundFile = File(currentSoundRecord?.path)
            soundFile.delete()

            currentSoundRecord = null
        }

        mediaRecorder = null
    }

    override fun getResultRecord(): SoundRecord? {
        val record = currentSoundRecord
        currentSoundRecord = null
        return record
    }

    override fun notifyRecordingListener(record: SoundRecord) {
        recordingListener.onNext(record)
    }

    override fun getRecordingListener(): Observable<SoundRecord> {
        return recordingListener
    }

    private fun addRecordOffset(soundRecord: SoundRecord) {
        val source = File(soundRecord.path)
        val tempFile = File(source.path + "_temp.aac")
        source.copyTo(tempFile, true)

        FFmpeg.execute("-y f lavfi -i sine=f=220:b=4:d=5 -c:a libvorbis ${source.path}silence.aac", {
            FFmpeg.execute("-y -f concat -i ${source.path}silence.aac -i ${tempFile.path} ${source.path}", {
                Log.d("FFMPEG", "callback")
                tempFile.delete()

                val mp = MediaPlayer()
                try {
                    mp.setDataSource(App.getContext()!!,Uri.parse(source.path))
                    mp.prepare()
                    mp.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {
                Log.d("FFMPEG", it)
            })
        }, {
            Log.d("FFMPEG", it)
        })
    }
}