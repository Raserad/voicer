package com.raserad.voicer.presentation.ui.editor

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.raserad.voicer.presentation.ui.editor.entities.SoundRecordViewData
import kotlinx.android.synthetic.main.view_sound_track_item.view.*


class RecordViewHolder(
    private val view: View,
    private val soundTrackListener: ((position: Int, actions: SoundRecordActions) -> Unit)?
): RecyclerView.ViewHolder(view) {

    init {
        view.disableButton.setOnClickListener {
            soundTrackListener?.invoke(adapterPosition, SoundRecordActions.DISABLE)
        }

        view.enableButton.setOnClickListener {
            soundTrackListener?.invoke(adapterPosition, SoundRecordActions.ENABLE)
        }

        view.deleteButton.setOnClickListener {
            soundTrackListener?.invoke(adapterPosition, SoundRecordActions.REMOVE)
        }
    }

    fun configure(soundRecordViewData: SoundRecordViewData) {
        view.disableButton.visibility = if(soundRecordViewData.isEnabled) View.VISIBLE else View.GONE
        view.enableButton.visibility = if(soundRecordViewData.isEnabled) View.GONE else View.VISIBLE

        view.disabledMask.visibility = if(soundRecordViewData.isEnabled) View.GONE else View.VISIBLE

        view.text.text = "total: ${soundRecordViewData.total}, start: ${soundRecordViewData.start}, end: ${soundRecordViewData.end}"

        view.soundTack.initTrackData(soundRecordViewData.total, soundRecordViewData.start, soundRecordViewData.end)
    }
}