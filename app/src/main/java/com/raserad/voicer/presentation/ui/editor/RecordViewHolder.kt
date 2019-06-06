package com.raserad.voicer.presentation.ui.editor

import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.raserad.voicer.presentation.ui.editor.entities.SoundRecordViewData
import kotlinx.android.synthetic.main.view_sound_track_item.view.*


class RecordViewHolder(
    private val view: View,
    private val soundTrackListener: ((position: Int, actions: SoundTrackActions) -> Unit)?,
    private val recyclerWidth: Int
): RecyclerView.ViewHolder(view) {

    init {
        view.disableButton.setOnClickListener {
            soundTrackListener?.invoke(adapterPosition, SoundTrackActions.DISABLE)
        }

        view.enableButton.setOnClickListener {
            soundTrackListener?.invoke(adapterPosition, SoundTrackActions.ENABLE)
        }

        view.deleteButton.setOnClickListener {
            soundTrackListener?.invoke(adapterPosition, SoundTrackActions.REMOVE)
        }
    }

    fun configure(soundRecordViewData: SoundRecordViewData) {
        view.disabledMask.visibility = if(soundRecordViewData.isEnabled) View.GONE else View.VISIBLE
        view.disableButton.visibility = if(soundRecordViewData.isEnabled) View.GONE else View.VISIBLE
        view.enableButton.visibility = if(soundRecordViewData.isEnabled) View.VISIBLE else View.GONE

        val start = soundRecordViewData.start.toFloat() / soundRecordViewData.total.toFloat() * 100f
        val end = (soundRecordViewData.total.toFloat() - soundRecordViewData.end.toFloat()) / soundRecordViewData.total.toFloat() * 100f
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        params.setMargins((recyclerWidth.toFloat() / 100 * start).toInt(), 0, (recyclerWidth.toFloat() / 100 * end).toInt(), 0)
        view.soundTack.layoutParams = params
    }
}