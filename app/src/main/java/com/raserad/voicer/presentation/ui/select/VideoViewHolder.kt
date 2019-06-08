package com.raserad.voicer.presentation.ui.select

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.raserad.voicer.presentation.ui.select.entities.VideoPreviewViewData
import kotlinx.android.synthetic.main.view_video_item.view.*

class VideoViewHolder(private val view: View, private val videoSelectListener: ((index: Int) -> Unit)?): RecyclerView.ViewHolder(view) {

    fun configure(video: VideoPreviewViewData) {
        view.setOnClickListener {
            if(!video.isSelected) {
                videoSelectListener?.invoke(adapterPosition)
            }
        }

        view.imageView.setImageBitmap(video.preview)

        view.selectedMask.visibility = if(video.isSelected) View.VISIBLE else View.GONE
    }
}