package com.raserad.voicer.presentation.mvp.list

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.raserad.voicer.domain.project.entities.Project

interface ProjectListView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showList(list: MutableList<Project>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmpty(isShow: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRemoveCancelAction(isShow: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showProjectRemove(position: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showProjectInsert(position: Int, project: Project)
}