package com.raserad.voicer.presentation.ui.select

import android.app.Activity
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
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

        if(video.preview != null) {
            view.imageView.setImageBitmap(video.preview)
        }
        else {
            Thread(Runnable {
                val bitmap = ThumbnailUtils.createVideoThumbnail(video.path, MediaStore.Images.Thumbnails.MINI_KIND)

                (view.context as Activity).runOnUiThread{
                    video.preview = bitmap
                    view.imageView.setImageBitmap(video.preview)
                }
            }).start()
        }


        view.selectedMask.visibility = if(video.isSelected) View.VISIBLE else View.GONE
    }
}