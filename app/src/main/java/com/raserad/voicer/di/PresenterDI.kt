package com.raserad.voicer.di

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.mvp.create.ProjectCreatePresenter
import com.raserad.voicer.presentation.mvp.create.ProjectCreateView
import com.raserad.voicer.presentation.mvp.editor.ProjectEditorPresenter
import com.raserad.voicer.presentation.mvp.editor.ProjectEditorView
import com.raserad.voicer.presentation.mvp.list.ProjectListPresenter
import com.raserad.voicer.presentation.mvp.list.ProjectListView
import com.raserad.voicer.presentation.mvp.start.StartPresenter
import com.raserad.voicer.presentation.utils.SubscribeManager

class PresenterDI(private val interactorDI: InteractorDI) {

    private fun getSubscribeManager() = SubscribeManager()

    fun getStart() = StartPresenter(
        interactorDI.getProjectRemove(),
        interactorDI.getRemoveSound()
    )

    fun getProjectList(view: ProjectListView, router: Router) = ProjectListPresenter(
        view,
        interactorDI.getProjectList(),
        interactorDI.getProjectRemove(),
        interactorDI.getProjectSharing(),
        interactorDI.getProjectListener(),
        getSubscribeManager(),
        router
    )

    fun getProjectCreate(view: ProjectCreateView, router: Router) = ProjectCreatePresenter(
        view,
        interactorDI.getVideo(),
        interactorDI.getProjectCreate(),
        getSubscribeManager(),
        router
    )

    fun getProjectEditor(view: ProjectEditorView, router: Router, project: Project) = ProjectEditorPresenter(
        view,
        interactorDI.getSound(),
        interactorDI.getSoundRecord(),
        interactorDI.getRemoveSound(),
        interactorDI.getReleaseVideo(),
        interactorDI.getVideoGenerate(),
        interactorDI.getProjectSharing(),
        getSubscribeManager(),
        router,
        project
    )
}