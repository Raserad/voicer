package com.raserad.voicer.presentation.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.raserad.voicer.R
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.presentation.mvp.list.ProjectListPresenter
import com.raserad.voicer.presentation.mvp.list.ProjectListView
import com.raserad.voicer.presentation.ui.list.entities.ProjectViewData
import kotlinx.android.synthetic.main.fragment_project_list.*
import kotlinx.android.synthetic.main.fragment_project_list.view.*

class ProjectListFragment: Fragment(), ProjectListView {

    var presenter: ProjectListPresenter? = null

    private var innerView: View? = null

    private val projectListAdapter = ProjectListAdapter()

    private var deleteSnackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(innerView == null) {
            innerView = inflater.inflate(R.layout.fragment_project_list, container, false)
        }
        return innerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null) {
            configure()
            presenter?.onStart()
        }
    }

    override fun onDetach() {
        super.onDetach()
        if(isRemoving) {
            presenter?.onFinish()
        }
    }

    private fun configure() {
        projectListAdapter.projectActionsListener = { position, action ->
            when(action) {
                ProjectListActions.EDIT -> presenter?.showProjectEditor(position)
                ProjectListActions.REMOVE -> presenter?.removeProject(position)
                ProjectListActions.SHARE -> presenter?.shareProject(position)
            }
        }

        listView.adapter = projectListAdapter
        listView.layoutManager = LinearLayoutManager(activity)

        projectCreateButton.setOnClickListener {
            presenter?.showProjectCreate()
        }
    }

    override fun showProjectRemove(position: Int) {
        projectListAdapter.list.removeAt(position)
        projectListAdapter.notifyItemRemoved(position)
    }

    override fun showProjectChange(project: Project) {

    }

    override fun showProjectInsert(position: Int, project: Project) {
        val projectViewData = ProjectViewData(project.title, project.description, project.video, null)
        projectListAdapter.list.add(position, projectViewData)
        projectListAdapter.notifyItemInserted(position)
    }

    override fun showList(list: MutableList<Project>) {
        listView.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE

        val preparedList: MutableList<ProjectViewData> = ArrayList()
        list.forEach {project ->
            val projectViewData = ProjectViewData(project.title, project.description, project.video, null)
            preparedList.add(projectViewData)
        }

        projectListAdapter.list = preparedList
        projectListAdapter.notifyDataSetChanged()
    }

    override fun showEmpty(isShow: Boolean) {
        emptyView.visibility = if(isShow) View.VISIBLE else View.GONE
    }

    override fun showRemoveCancelAction(isShow: Boolean) {
        deleteSnackbar?.dismiss()

        if(isShow && view != null) {
            deleteSnackbar = Snackbar.make(view!!, R.string.project_removed_notification, Snackbar.LENGTH_INDEFINITE)
            deleteSnackbar?.setAction(R.string.project_removed_cancel) {
                presenter?.cancelProjectRemoving()
            }
            deleteSnackbar?.show()
        }
    }
}