package com.raserad.voicer.domain.project.list

import com.raserad.voicer.domain.project.entities.Project
import io.reactivex.Observable

interface ProjectListRepository {

    fun getList(): Observable<MutableList<Project>>
}