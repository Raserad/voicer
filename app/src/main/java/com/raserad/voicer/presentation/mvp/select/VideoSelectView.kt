package com.raserad.voicer.presentation.mvp.select

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.raserad.voicer.domain.video.entities.Video

interface VideoSelectView: MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun checkVideoListPermissions()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showVideoList(list: List<Video>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSelectedVideo(index: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmpty(isShow: Boolean)
}