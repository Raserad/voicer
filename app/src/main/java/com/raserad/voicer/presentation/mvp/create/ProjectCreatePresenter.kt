package com.raserad.voicer.presentation.mvp.create

import android.util.Log
import com.raserad.voicer.domain.project.create.ProjectCreateInteractor
import com.raserad.voicer.domain.project.create.entities.ProjectCreateData
import com.raserad.voicer.domain.video.VideoInteractor
import com.raserad.voicer.domain.video.entities.Video
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.mvp.Presenter
import com.raserad.voicer.presentation.utils.SubscribeManager

class ProjectCreatePresenter(
    private val view: ProjectCreateView,
    private val videoInteractor: VideoInteractor,
    private val projectCreateInteractor: ProjectCreateInteractor,
    private val subscribeManager: SubscribeManager,
    private val router: Router
): Presenter {

    private var list: List<Video> = ArrayList()

    private var projectCreateData = ProjectCreateData("", "", "", 0, 0)

    override fun onStart() {}

    override fun onFinish() {
        subscribeManager.unsubscribeAll()
    }

    fun getVideoList() {
        val videoListGetting = videoInteractor.getList()
            .doOnNext {list ->
                this.list = list
                view.showVideoList(list)
                view.showEmpty(list.isEmpty())

                if(list.isNotEmpty()) {
                    view.showSelectedVideo(0)
                }
            }

        subscribeManager.subscribe(videoListGetting)
    }

    fun cancelCreating() {
        router.back()
    }

    fun selectVideo(index: Int) {
        view.showSelectedVideo(index)
    }

    fun rememberProjectTitle(title: String) {
        projectCreateData.title = title
    }

    fun rememberProjectDescription(description: String) {
        projectCreateData.description = description
    }

    fun rememberVideoTrimData(videoPath: String?, startTime: Int, endTime: Int) {
        if(videoPath != null) projectCreateData.videoPath = videoPath
        projectCreateData.videoStartTime = startTime
        projectCreateData.videoEndTime = endTime
    }

    fun createProject() {
        view.showCreateProgress(true)
        val projectCreating = projectCreateInteractor.create(projectCreateData)
            .doOnNext {project ->
                Log.d("PROJECT_VIDEO_PATH", project.video)
                view.showCreateProgress(false)
                router.showProjectEditor(project)
            }

        subscribeManager.subscribe(projectCreating)
    }
}