package com.raserad.voicer.presentation.mvp.select

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.raserad.voicer.domain.video.VideoInteractor
import com.raserad.voicer.domain.video.entities.Video
import com.raserad.voicer.domain.video.entities.VideoTrimData
import com.raserad.voicer.domain.video.trim.VideoTrimInteractor
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.utils.SubscribeManager

@InjectViewState
class VideoSelectPresenter(
    private val videoInteractor: VideoInteractor,
    private val videoTrimInteractor: VideoTrimInteractor,
    private val subscribeManager: SubscribeManager,
    private val router: Router
): MvpPresenter<VideoSelectView>() {

    private val view = viewState

    private var list: List<Video> = ArrayList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        view.checkVideoListPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
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

    fun rememberTrimData(path: String, start: Long, end: Long) {
        videoTrimInteractor.remember(VideoTrimData(path, start, end))
    }

    fun showCreate() {
        router.showProjectCreate()
    }
}