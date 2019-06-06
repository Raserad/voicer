package com.raserad.voicer.presentation.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raserad.voicer.R
import com.raserad.voicer.presentation.ui.list.entities.ProjectViewData

class ProjectListAdapter: RecyclerView.Adapter<ProjectViewHolder>() {

    var list: MutableList<ProjectViewData> = ArrayList()

    var projectActionsListener: ((position: Int, actions: ProjectListActions) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_project_item, parent, false)
        return ProjectViewHolder(view, projectActionsListener)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(projectViewHolder: ProjectViewHolder, position: Int) {
        projectViewHolder.configure(list[position])
    }
}