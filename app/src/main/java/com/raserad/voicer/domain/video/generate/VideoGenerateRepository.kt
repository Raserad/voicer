package com.raserad.voicer.domain.video.generate

import com.raserad.voicer.domain.project.entities.Project
import io.reactivex.Observable

interface VideoGenerateRepository {

    fun generateVideo(project: Project): Observable<Boolean>
}