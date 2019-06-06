package com.raserad.voicer.presentation.mvp.list

import com.raserad.voicer.domain.project.entities.Project

interface ProjectListView {

    fun showList(list: MutableList<Project>)

    fun showEmpty(isShow: Boolean)

    fun showRemoveCancelAction(isShow: Boolean)

    fun showProjectRemove(position: Int)

    fun showProjectChange(project: Project)

    fun showProjectInsert(position: Int, project: Project)
}