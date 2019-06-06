package com.raserad.voicer.presentation

import com.raserad.voicer.domain.project.entities.Project

interface Router {

    fun start()

    fun showProjectList()

    fun showProjectCreate()

    fun showProjectEditor(project: Project)

    fun back()
}