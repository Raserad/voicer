package com.raserad.voicer.domain.sound.record

import kotlin.random.Random

class SoundRecordInteractor(
    private val soundRecordRepository: SoundRecordRepository
) {

    fun startRecording(time: Long) {
        soundRecordRepository.startRecording(generateId(), time)
    }

    fun stopRecording(time: Long, totalTime: Long) {
        soundRecordRepository.stopRecording(time, totalTime)
        val record = soundRecordRepository.getResultRecord()
        if(record != null) {
            soundRecordRepository.notifyRecordingListener(record)
        }
    }

    fun getRecordingListener() = soundRecordRepository.getRecordingListener()

    private fun generateId(): Int {
        val charPool : List<Char> = arrayListOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')
        return (1..7)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("").toInt()
    }
}