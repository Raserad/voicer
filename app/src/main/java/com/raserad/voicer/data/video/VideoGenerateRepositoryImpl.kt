package com.raserad.voicer.data.video

import android.media.AudioFormat
import android.media.AudioFormat.CHANNEL_OUT_MONO
import android.media.AudioManager
import android.media.AudioTrack
import com.github.piasy.audio_mixer.AudioMixer
import com.github.piasy.audio_mixer.MixerConfig
import com.github.piasy.audio_mixer.MixerSource
import com.raserad.voicer.data.sound.entities.SoundRecordObject
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.video.generate.VideoGenerateRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

class VideoGenerateRepositoryImpl: VideoGenerateRepository {

    override fun generateVideo(project: Project): Observable<Boolean> {
        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()

            val recordObjects = realm.where(SoundRecordObject::class.java).equalTo("uid", project.uid).findAll()

            val records: MutableList<SoundRecord> = ArrayList()
            recordObjects.forEach {recordObject ->
                if(!recordObject.isEnabled) return@forEach
                records.add(SoundRecord(recordObject.id, recordObject.path, recordObject.start, recordObject.end, recordObject.total, recordObject.isEnabled))
            }

            realm.close()

            val tracks: MutableList<MixerSource> = ArrayList()

            tracks.add(MixerSource(MixerSource.TYPE_FILE, 1, 1f,"${project.video}.aac", 0, 0))

            records.forEach {track ->
                tracks.add(MixerSource(MixerSource.TYPE_FILE, 1, 1f, track.path, 0, 0))
            }

            val mixer = AudioMixer(MixerConfig(ArrayList(tracks), 48000, 1, 5))

            val buffer = mixer.mix()

            if (buffer.size > 0) {
                val sampleRate = 48000

                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC, sampleRate,
                    CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    Math.max(buffer.size, AudioMixer.MAX_BUF_SIZE),
                    AudioTrack.MODE_STREAM
                )
                audioTrack.write(buffer.buffer, 0, buffer.size)

                audioTrack.play()
            }

        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
    }
}