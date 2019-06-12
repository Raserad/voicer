package com.raserad.voicer.presentation.ui.list

import android.content.res.Configuration
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.snackbar.Snackbar
import com.raserad.voicer.R
import com.raserad.voicer.di.AppDI
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.presentation.mvp.list.ProjectListPresenter
import com.raserad.voicer.presentation.mvp.list.ProjectListView
import com.raserad.voicer.presentation.ui.list.entities.ProjectViewData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_project_list.*

class ProjectListFragment: MvpAppCompatFragment(), ProjectListView {

    @InjectPresenter
    lateinit var presenter: ProjectListPresenter

    @ProvidePresenter
    fun providePresenter() = AppDI.getPresenterDI()
        .getProjectList(AppDI.getRouter())

    private val projectListAdapter = ProjectListAdapter()

    private var deleteSnackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        projectListAdapter.projectActionsListener = { position, action ->
            when(action) {
                ProjectListActions.EDIT -> presenter.showProjectEditor(position)
                ProjectListActions.REMOVE -> presenter.removeProject(position)
                ProjectListActions.SHARE -> presenter.shareProject(position)
            }
        }

        listView.adapter = projectListAdapter

        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            listView.layoutManager = LinearLayoutManager(activity)
        } else {
            listView.layoutManager = GridLayoutManager(activity!!, 2)
        }

        projectCreateButton.setOnClickListener {
            presenter.showProjectCreate()
        }
    }

    override fun showProjectRemove(position: Int) {
        projectListAdapter.list.removeAt(position)
        projectListAdapter.notifyItemRemoved(position)
    }

    override fun showProjectInsert(position: Int, project: Project) {
        val projectViewData = ProjectViewData(project.title, project.description, project.video, null)
        projectListAdapter.list.add(position, projectViewData)
        Observable.create<Boolean> { oberver ->
            val bitmap = ThumbnailUtils.createVideoThumbnail(project.video, MediaStore.Images.Thumbnails.MINI_KIND)
            projectViewData.preview = bitmap
            oberver.onNext(true)
        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext {
            projectListAdapter.notifyItemChanged(position)
        }
        .subscribe()
        projectListAdapter.notifyItemInserted(position)
        listView.scrollToPosition(position)
    }

    override fun showList(list: MutableList<Project>) {
        listView.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE

        val preparedList: MutableList<ProjectViewData> = ArrayList()

        var counter = 0
        list.forEach {project ->
            val projectViewData = ProjectViewData(project.title, project.description, project.video, null)
            preparedList.add(projectViewData)

            val position = counter
            Observable.create<Boolean> { oberver ->
                val bitmap = ThumbnailUtils.createVideoThumbnail(project.video, MediaStore.Images.Thumbnails.MINI_KIND)
                projectViewData.preview = bitmap
                oberver.onNext(true)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                projectListAdapter.notifyItemChanged(position)
            }
            .subscribe()
            counter++
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
                presenter.cancelProjectRemoving()
            }
            deleteSnackbar?.show()
        }
    }
}