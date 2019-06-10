package com.raserad.voicer.data.video

<<<<<<< HEAD
import android.util.Log
import com.raserad.voicer.data.sound.entities.SoundRecordObject
import com.raserad.voicer.data.sound.entities.SoundRecordRemoveObject
import com.raserad.voicer.data.utils.FFmpeg
=======
import android.media.AudioFormat
import android.media.AudioFormat.CHANNEL_OUT_MONO
import android.media.AudioManager
import android.media.AudioTrack
import com.github.piasy.audio_mixer.AudioMixer
import com.github.piasy.audio_mixer.MixerConfig
import com.github.piasy.audio_mixer.MixerSource
import com.raserad.voicer.data.sound.entities.SoundRecordObject
>>>>>>> 55fdb3127cd560e4470c52322e1e45455a9530b7
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.video.generate.VideoGenerateRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
<<<<<<< HEAD
import java.io.File
=======
>>>>>>> 55fdb3127cd560e4470c52322e1e45455a9530b7

class VideoGenerateRepositoryImpl: VideoGenerateRepository {

    override fun generateVideo(project: Project): Observable<Boolean> {
        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()
<<<<<<< HEAD

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

            command += " -filter_complex amix=inputs=${records.count() + 1}:duration=first:dropout_transition=${records.count() + 1};[1]adelay=5|5 ${project.video}_temp.aac"
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
=======

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

>>>>>>> 55fdb3127cd560e4470c52322e1e45455a9530b7
        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
    }
}