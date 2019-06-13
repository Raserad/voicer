package com.raserad.voicer.data.project

import com.raserad.voicer.data.project.entities.ProjectObject
import com.raserad.voicer.domain.project.create.ProjectCreateRepository
import com.raserad.voicer.domain.project.entities.Project
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

class ProjectCreateRepositoryImpl: ProjectCreateRepository {

    override fun create(project: Project): Observable<Boolean> {
        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction {db ->
                val projectObject = ProjectObject()
                projectObject.uid = project.uid
                projectObject.title = project.title
                projectObject.description = project.description
                projectObject.video = project.video
                db.insert(projectObject)
            }

            realm.close()
            observer.onNext(true)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
}