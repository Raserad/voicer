package com.raserad.voicer.presentation.ui.list

import android.app.Activity
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.raserad.voicer.presentation.ui.list.entities.ProjectViewData
import kotlinx.android.synthetic.main.view_project_item.view.*

class ProjectViewHolder(private val view: View, private val projectActionListener: ((position: Int, actions: ProjectListActions) -> Unit)?): RecyclerView.ViewHolder(view) {

    init {
        view.editProjectButton.setOnClickListener {
            projectActionListener?.invoke(adapterPosition, ProjectListActions.EDIT)
        }
        view.removeProjectButton.setOnClickListener {
            projectActionListener?.invoke(adapterPosition, ProjectListActions.REMOVE)
        }
        view.shareProjectButton.setOnClickListener {
            projectActionListener?.invoke(adapterPosition, ProjectListActions.SHARE)
        }
    }

    fun configure(project: ProjectViewData) {
        view.titleView.text = project.title
        view.descriptionView.text = project.description

        if(project.preview != null) {
            view.imageView.setImageBitmap(project.preview)
        }
        else {
            Thread(Runnable {
                val bitmap = ThumbnailUtils.createVideoThumbnail(project.video, MediaStore.Images.Thumbnails.MINI_KIND)

                (view.context as Activity).runOnUiThread{
                    project.preview = bitmap
                    view.imageView.setImageBitmap(project.preview)
                }
            }).start()
        }
    }
}