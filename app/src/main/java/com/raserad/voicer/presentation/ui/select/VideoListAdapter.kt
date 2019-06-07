package com.raserad.voicer.presentation.ui.select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raserad.voicer.R
import com.raserad.voicer.presentation.ui.select.entities.VideoPreviewViewData
import java.lang.Exception

class VideoListAdapter: RecyclerView.Adapter<VideoViewHolder>() {

    var list: List<VideoPreviewViewData> = ArrayList()

    var videoSelectListener: ((index: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_video_item, parent, false)
        return VideoViewHolder(view, videoSelectListener)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.configure(list[position])
    }

    fun selectVideo(position: Int) {
        try {
            val selected = list.first { it.isSelected }
            selected.isSelected = false
            notifyItemChanged(list.indexOf(selected))
        }
        catch(e: Exception) {}
        list[position].isSelected = true
        notifyItemChanged(position)
    }
}