package com.raserad.voicer.domain.video.trim

import com.raserad.voicer.domain.video.entities.VideoTrimData

class VideoTrimInteractor(
    private val videoTrimRepository: VideoTrimRepository
) {

    fun remember(trimData: VideoTrimData) = videoTrimRepository.remember(trimData)

    fun getRemembered() = videoTrimRepository.getRemembered()
}