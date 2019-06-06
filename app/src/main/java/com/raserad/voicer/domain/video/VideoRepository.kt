package com.raserad.voicer.domain.video

import com.raserad.voicer.domain.video.entities.Video
import io.reactivex.Observable

interface VideoRepository {

    fun getList(): Observable<List<Video>>

    fun trimVideo(path: String, startTime: Int, endTime: Int): Observable<Video>
}