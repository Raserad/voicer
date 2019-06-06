package com.raserad.voicer.presentation.mvp.create

import com.raserad.voicer.domain.video.entities.Video

interface ProjectCreateView {

    fun showVideoList(list: List<Video>)

    fun showSelectedVideo(index: Int)

    fun showEmpty(isShow: Boolean)

    fun showCreateProgress(isShow: Boolean)
}