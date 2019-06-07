package com.raserad.voicer.data.video

import android.net.Uri
import android.util.Log
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

class VideoTrimRepositoryImpl: VideoTrimRepository {

    private var trimData: VideoTrimData? = null

    override fun trim(trimData: VideoTrimData): Observable<Video> {
        return Observable.create<Video> { observer ->

            val projectsDirectory = File(App.getContext()!!.filesDir.path + "/projects")
            projectsDirectory.mkdir()

            val sourceVideo = File(trimData.path)

            TrimVideoUtils.startTrim(sourceVideo, projectsDirectory.path + "/", trimData.startTime.toLong(), trimData.endTime.toLong(), object:
                OnTrimVideoListener {
                override fun getResult(uri: Uri) {
                    if(uri.path != null) {
                        val video = Video(uri.path!!)
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