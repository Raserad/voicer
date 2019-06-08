package com.raserad.voicer.presentation.mvp.editor

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo

interface ProjectEditorView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showSoundRecordMessage(isShow: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun playVideo(isPlaying: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showVideo(video: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRecordList(list: MutableList<SoundRecord>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRecordsEmpty(isShow: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showRecordInsert(position: Int, record: SoundRecord)

    @StateStrategyType(SkipStrategy::class)
    fun showRecordRemove(position: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRecordEnabled(position: Int, isEnabled: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRecordRemoveCancelAction(isShow: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRecordingFinish()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showVideoGeneratingProgress(isShow: Boolean)
}