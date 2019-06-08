package com.raserad.voicer.domain.project.remove

import com.raserad.voicer.domain.project.entities.Project
import io.reactivex.Observable

interface ProjectRemoveRepository {

    fun markToRemove(project: Project): Observable<Boolean>

    fun unmarkMarked(): Observable<Boolean>

    fun removeMarked(): Observable<Boolean>
}