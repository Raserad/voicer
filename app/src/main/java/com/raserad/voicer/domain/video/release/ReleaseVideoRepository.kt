package com.raserad.voicer.domain.video.release

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo
import io.reactivex.Observable

interface ReleaseVideoRepository {

    fun generateReleaseVideo(project: Project): Observable<ReleaseVideo>

    fun getReleaseVideo(project: Project): Observable<ReleaseVideo>
}