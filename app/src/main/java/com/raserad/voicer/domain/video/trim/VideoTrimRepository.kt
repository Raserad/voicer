package com.raserad.voicer.domain.video.trim

import com.raserad.voicer.domain.video.entities.Video
import com.raserad.voicer.domain.video.entities.VideoTrimData
import io.reactivex.Observable

interface VideoTrimRepository {

    fun trim(trimData: VideoTrimData): Observable<Video>

    fun remember(trimData: VideoTrimData)

    fun getRemembered(): VideoTrimData?
}