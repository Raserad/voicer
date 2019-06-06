package com.raserad.voicer.domain.project.remove

import com.raserad.voicer.domain.project.entities.Project

interface ProjectRemoveRepository {

    fun markToRemove(project: Project)

    fun unmarkMarked()

    fun removeMarked()
}