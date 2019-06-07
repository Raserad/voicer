package com.raserad.voicer.presentation.mvp.create

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.raserad.voicer.domain.project.broadcast.ProjectBroadcastInteractor
import com.raserad.voicer.domain.project.create.ProjectCreateInteractor
import com.raserad.voicer.domain.project.create.entities.ProjectCreateData
import com.raserad.voicer.domain.video.trim.VideoTrimInteractor
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.utils.SubscribeManager

@InjectViewState
class ProjectCreatePresenter(
    private val projectCreateInteractor: ProjectCreateInteractor,
    private val projectBroadcastInteractor: ProjectBroadcastInteractor,
    videoTrimInteractor: VideoTrimInteractor,
    private val subscribeManager: SubscribeManager,
    private val router: Router
): MvpPresenter<ProjectCreateView>() {

    private val view = viewState

    private val trimData = videoTrimInteractor.getRemembered()

    private var title: String? = null
    private var description: String? = null

    override fun onDestroy() {
        super.onDestroy()
        subscribeManager.unsubscribeAll()
    }

    fun rememberTitle(title: String) {
        this.title = title.trim()
        view.showTitleEmptyError(false)
    }

    fun rememberDescription(description: String) {
        this.description = description.trim()
    }

    fun create() {
        view.showTitleEmptyError(title == null)

        val trimData = trimData ?: return
        val title = title ?: return
        val description = description ?: ""

        view.showCreateProgress(true)
        val projectCreateData = ProjectCreateData(title, description, trimData)
        val projectCreating = projectCreateInteractor.create(projectCreateData)
            .doOnNext {project ->
                view.dismiss()
                projectBroadcastInteractor.remember(project)
                router.showProjectEditor()
            }

        subscribeManager.subscribe(projectCreating)
    }
}