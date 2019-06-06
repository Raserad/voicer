package com.raserad.voicer.di

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.raserad.voicer.R
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.presentation.ui.create.ProjectCreateFragment
import com.raserad.voicer.presentation.ui.editor.ProjectEditorFragment
import com.raserad.voicer.presentation.ui.list.ProjectListFragment

class RouterDI(var activity: Activity, private val presenterDI: PresenterDI): Router {

    private fun fragmentManager() = (activity as AppCompatActivity).supportFragmentManager

    override fun start() {
        presenterDI.getStart().onStart()

        showProjectList()
    }

    override fun back() {
        fragmentManager().popBackStack()
    }

    override fun showProjectList() {
        val fragment = ProjectListFragment()

        fragment.presenter = presenterDI.getProjectList(fragment, this)

        fragmentManager()
            .beginTransaction()
            .add(R.id.container, fragment, "project_list")
            .commit()
    }

    override fun showProjectCreate() {
        val fragment = ProjectCreateFragment()

        fragment.presenter = presenterDI.getProjectCreate(fragment, this)

        fragmentManager()
            .beginTransaction()
            .addToBackStack("project_create")
            .setCustomAnimations(R.anim.slide_up, R.anim.stay, R.anim.stay, R.anim.slide_down)
            .add(R.id.container, fragment, "project_create")
            .commit()
    }

    override fun showProjectEditor(project: Project) {
        fragmentManager().popBackStackImmediate("project_create", FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val fragment = ProjectEditorFragment()

        fragment.presenter = presenterDI.getProjectEditor(fragment, this, project)

        fragmentManager()
            .beginTransaction()
            .addToBackStack("project_editor")
            .setCustomAnimations(R.anim.slide_right, R.anim.stay, R.anim.stay, R.anim.slide_left)
            .add(R.id.container, fragment, "project_editor")
            .commit()
    }
}