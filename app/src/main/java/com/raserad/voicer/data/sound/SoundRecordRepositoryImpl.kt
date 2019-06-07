package com.raserad.voicer.data.sound

import android.media.MediaRecorder
import com.raserad.voicer.App
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
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
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
}