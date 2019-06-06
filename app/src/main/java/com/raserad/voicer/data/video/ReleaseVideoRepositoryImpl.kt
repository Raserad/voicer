package com.raserad.voicer.data.video

import com.raserad.voicer.data.video.entites.ReleaseVideoObject
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.video.release.ReleaseVideoRepository
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo
import io.reactivex.Observable
import io.realm.Realm
import java.io.File

class ReleaseVideoRepositoryImpl: ReleaseVideoRepository {

    override fun generateReleaseVideo(project: Project): Observable<ReleaseVideo> {
        return Observable.create<ReleaseVideo> {observer ->
            val source = File(project.video)

            val releasePath = project.video + "_release.mp4"

            val copy = File(releasePath)

            source.copyTo(copy, true)

            val realm = Realm.getDefaultInstance()

            realm.executeTransaction {db ->

                db.where(ReleaseVideoObject::class.java).equalTo("uid", project.uid).findAll().deleteAllFromRealm()

                val data = ReleaseVideoObject()
                data.uid = project.uid
                data.path = releasePath

                db.insert(data)
            }

            observer.onNext(ReleaseVideo(releasePath))

            realm.close()
        }
    }

    override fun getReleaseVideo(project: Project): Observable<ReleaseVideo> {
        return Observable.create<ReleaseVideo> {observer ->
            val realm = Realm.getDefaultInstance()

            val video = realm.where(ReleaseVideoObject::class.java).equalTo("uid", project.uid).findFirst()

            if(video != null) {
                val videoFile = File(video.path)
                if(!videoFile.exists()) {
                    observer.onNext(ReleaseVideo(""))
                }
                else {
                    observer.onNext(ReleaseVideo(video.path))
                }
            }
            else {
                observer.onNext(ReleaseVideo(""))
            }

            realm.close()
        }
    }
}