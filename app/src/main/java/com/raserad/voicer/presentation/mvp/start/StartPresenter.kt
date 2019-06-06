package com.raserad.voicer.presentation.mvp.start

import com.raserad.voicer.domain.project.remove.ProjectRemoveInteractor
import com.raserad.voicer.domain.sound.remove.RemoveSoundInteractor
import com.raserad.voicer.presentation.mvp.Presenter

class StartPresenter(
    private val projectRemoveInteractor: ProjectRemoveInteractor,
    private val removeSoundInteractor: RemoveSoundInteractor
): Presenter {

    override fun onStart() {
        projectRemoveInteractor.removeMarked()
        removeSoundInteractor.removeMarked()
    }

    override fun onFinish() {}
}