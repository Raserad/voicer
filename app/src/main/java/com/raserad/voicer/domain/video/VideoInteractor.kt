package com.raserad.voicer.domain.video

class VideoInteractor(private val videoRepository: VideoRepository) {

    fun getList() = videoRepository.getList()
}