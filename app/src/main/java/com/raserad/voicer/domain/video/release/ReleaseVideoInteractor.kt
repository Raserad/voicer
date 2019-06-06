package com.raserad.voicer.domain.video.release

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo
import io.reactivex.Observable

class ReleaseVideoInteractor(
    private val releaseVideoRepository: ReleaseVideoRepository
) {

    fun getVideo(project: Project): Observable<ReleaseVideo> {
        return releaseVideoRepository.getReleaseVideo(project)
            .flatMap {releaseVideo ->
                if(releaseVideo.path.isNotEmpty()) {
                    Observable.just(releaseVideo)
                }
                else {
                    releaseVideoRepository.generateReleaseVideo(project)
                }
            }
    }
}