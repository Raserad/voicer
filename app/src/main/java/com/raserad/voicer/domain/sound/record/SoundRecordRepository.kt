package com.raserad.voicer.domain.sound.record

import com.raserad.voicer.domain.sound.entities.SoundRecord
import io.reactivex.Observable

interface SoundRecordRepository {

    fun startRecording(id: Int, time: Long)

    fun stopRecording(time: Long, totalTime: Long)

    fun getResultRecord(): SoundRecord?

    fun notifyRecordingListener(record: SoundRecord)

    fun getRecordingListener(): Observable<SoundRecord>
}