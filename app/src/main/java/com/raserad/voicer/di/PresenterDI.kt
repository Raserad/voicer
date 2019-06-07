package com.raserad.voicer.di

import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.mvp.create.ProjectCreatePresenter
import com.raserad.voicer.presentation.mvp.select.VideoSelectPresenter
import com.raserad.voicer.presentation.mvp.editor.ProjectEditorPresenter
import com.raserad.voicer.presentation.mvp.list.ProjectListPresenter
import com.raserad.voicer.presentation.mvp.start.StartPresenter
import com.raserad.voicer.presentation.utils.SubscribeManager

class PresenterDI(private val interactorDI: InteractorDI) {

    private fun getSubscribeManager() = SubscribeManager()

    fun getStart() = StartPresenter(
        interactorDI.getProjectRemove(),
        interactorDI.getRemoveSound()
    )

    fun getProjectList(router: Router) = ProjectListPresenter(
        interactorDI.getProjectList(),
        interactorDI.getProjectRemove(),
        interactorDI.getProjectSharing(),
        interactorDI.getProjectListener(),
        interactorDI.getTempProject(),
        getSubscribeManager(),
        router
    )

    fun getVideoSelect(router: Router) = VideoSelectPresenter(
        interactorDI.getVideo(),
        interactorDI.getVideoTrim(),
        getSubscribeManager(),
        router
    )

    fun getProjectCreate(router: Router) = ProjectCreatePresenter(
        interactorDI.getProjectCreate(),
        interactorDI.getTempProject(),
        interactorDI.getVideoTrim(),
        getSubscribeManager(),
        router
    )

    fun getProjectEditor(router: Router) = ProjectEditorPresenter(
        interactorDI.getSound(),
        interactorDI.getSoundRecord(),
        interactorDI.getRemoveSound(),
        interactorDI.getReleaseVideo(),
        interactorDI.getVideoGenerate(),
        interactorDI.getProjectSharing(),
        interactorDI.getTempProject(),
        getSubscribeManager(),
        router
    )
}