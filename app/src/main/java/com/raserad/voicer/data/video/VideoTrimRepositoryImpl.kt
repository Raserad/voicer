package com.raserad.voicer.data.video

import android.net.Uri
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.raserad.videoutils.interfaces.OnTrimVideoListener
import com.raserad.videoutils.utils.TrimVideoUtils
import com.raserad.voicer.App
import com.raserad.voicer.domain.video.entities.Video
import com.raserad.voicer.domain.video.entities.VideoTrimData
import com.raserad.voicer.domain.video.trim.VideoTrimRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

class VideoTrimRepositoryImpl: VideoTrimRepository {

    private var trimData: VideoTrimData? = null

    override fun trim(trimData: VideoTrimData): Observable<Video> {
        return Observable.create<Video> { observer ->

            val projectsDirectory = File(App.getContext()!!.filesDir.path + "/projects")
            projectsDirectory.mkdir()

            TrimVideoUtils.startTrim(File(trimData.path), projectsDirectory.path + "/", trimData.startTime, trimData.endTime, object:
                OnTrimVideoListener {
                override fun getResult(uri: Uri) {
                    if(uri.path != null) {
                        val video = Video(uri.path!!)
                        val mWithVideo = MovieCreator.build(video.path)
                        val mWOutVideo = Movie()
                        for (track in mWithVideo.tracks) {
                            if (track.handler == "soun") {
                                mWOutVideo.addTrack(track)
                            }
                        }
                        val b = DefaultMp4Builder()
                        val c = b.build(mWOutVideo)
                        c.writeContainer(FileOutputStream("${video.path}.aac").channel)

                        observer.onNext(video)
                    }
                }

                override fun cancelAction() {

                }
            })
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }

    override fun remember(trimData: VideoTrimData) {
        this.trimData = trimData
    }

    override fun getRemembered(): VideoTrimData? {
        val trimData = this.trimData
        this.trimData = null
        return trimData
    }
}