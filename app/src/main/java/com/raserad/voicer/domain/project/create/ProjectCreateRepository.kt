package com.raserad.voicer.domain.project.create

import com.raserad.voicer.domain.project.entities.Project
import io.reactivex.Observable

interface ProjectCreateRepository {

    fun create(project: Project): Observable<Boolean>
}