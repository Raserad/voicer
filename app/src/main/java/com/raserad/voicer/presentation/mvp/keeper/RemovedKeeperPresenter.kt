package com.raserad.voicer.presentation.mvp.keeper

import com.raserad.voicer.domain.project.remove.ProjectRemoveInteractor
import com.raserad.voicer.domain.sound.remove.RemoveSoundInteractor
import com.raserad.voicer.presentation.utils.SubscribeManager

class RemovedKeeperPresenter(
    private val projectRemoveInteractor: ProjectRemoveInteractor,
    private val removeSoundInteractor: RemoveSoundInteractor,
    private val subscribeManager: SubscribeManager
) {

    fun keepRemoved(onFinish: () -> Unit = {}) {
        val unremovedProjectRemoving  = projectRemoveInteractor.removeMarked()
            .flatMap { removeSoundInteractor.removeMarked() }
            .doOnNext {
                onFinish()
                subscribeManager.unsubscribeAll()
            }

        subscribeManager.subscribe(unremovedProjectRemoving)
    }
}