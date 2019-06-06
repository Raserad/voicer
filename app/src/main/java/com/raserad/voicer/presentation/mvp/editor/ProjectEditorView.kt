package com.raserad.voicer.presentation.mvp.editor

import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo

interface ProjectEditorView {

    fun showSoundRecordMessage(isShow: Boolean)

    fun playVideo(isPlaying: Boolean)

    fun showVideo(video: ReleaseVideo)

    fun showRecordList(list: MutableList<SoundRecord>)

    fun showRecordsEmpty(isShow: Boolean)

    fun showRecordInsert(position: Int, record: SoundRecord)

    fun showRecordRemove(position: Int)

    fun showRecordEnabled(position: Int, isEnabled: Boolean)

    fun showRecordRemoveCancelAction(isShow: Boolean)

    fun showRecordingFinish()
}