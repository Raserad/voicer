package com.raserad.voicer.domain.video

import com.raserad.voicer.domain.video.entities.Video
import com.raserad.voicer.domain.video.entities.VideoTrimData
import io.reactivex.Observable

interface VideoRepository {

    fun getList(): Observable<List<Video>>
}