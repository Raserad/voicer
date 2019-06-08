package com.raserad.voicer.presentation.mvp.create

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface ProjectCreateView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showCreateProgress(isShow: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showKeyboard()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTitleEmptyError(isShow: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun dismiss()
}