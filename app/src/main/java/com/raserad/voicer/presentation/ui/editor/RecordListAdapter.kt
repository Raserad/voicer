package com.raserad.voicer.presentation.ui.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raserad.voicer.R
import com.raserad.voicer.presentation.ui.editor.entities.SoundRecordViewData

class RecordListAdapter: RecyclerView.Adapter<RecordViewHolder>() {

    var recyclerWidth: Int = 0
    var list: MutableList<SoundRecordViewData> = ArrayList()

    var soundTrackListener: ((position: Int, actions: SoundTrackActions) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_sound_track_item, parent, false)
        return RecordViewHolder(view, soundTrackListener, recyclerWidth)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.configure(list[position])
    }
}