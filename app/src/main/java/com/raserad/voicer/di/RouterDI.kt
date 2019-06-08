package com.raserad.voicer.di

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.raserad.voicer.R
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.ui.create.ProjectCreateDialog
import com.raserad.voicer.presentation.ui.select.VideoSelectFragment
import com.raserad.voicer.presentation.ui.editor.ProjectEditorFragment
import com.raserad.voicer.presentation.ui.list.ProjectListFragment

class RouterDI(var activity: Activity): Router {

    private fun fragmentManager() = (activity as AppCompatActivity).supportFragmentManager

    override fun start() {

        AppDI.getPresenterDI().getRemovedKeeper().keepRemoved()

        showProjectList()
    }

    override fun back() {
        fragmentManager().popBackStack()
    }

    override fun showProjectList() {
        val fragment = ProjectListFragment()

        fragmentManager()
            .beginTransaction()
            .add(R.id.container, fragment, "project_list")
            .commit()
    }

    override fun showProjectCreate() {
        val dialog = ProjectCreateDialog()

        dialog.show(fragmentManager(), "project_create")
    }

    override fun showVideSelect() {
        val fragment = VideoSelectFragment()

        fragmentManager()
            .beginTransaction()
            .addToBackStack("video_select")
            .setCustomAnimations(R.anim.slide_up, R.anim.stay, R.anim.stay, R.anim.slide_down)
            .add(R.id.container, fragment, "video_select")
            .commit()
    }

    override fun showProjectEditor() {
        fragmentManager().popBackStackImmediate("video_select", FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val fragment = ProjectEditorFragment()

        fragmentManager()
            .beginTransaction()
            .addToBackStack("project_editor")
            .setCustomAnimations(R.anim.slide_right, R.anim.stay, R.anim.stay, R.anim.slide_left)
            .add(R.id.container, fragment, "project_editor")
            .commit()
    }
}