package com.raserad.voicer.data.project

import com.raserad.voicer.data.project.entities.ProjectObject
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.list.ProjectListRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

class ProjectListRepositoryImpl: ProjectListRepository {

    override fun getList(): Observable<MutableList<Project>> {
        return Observable.create<MutableList<Project>> {observer ->

            val projects: MutableList<Project> = ArrayList()

            val realm = Realm.getDefaultInstance()

            val results = realm.where(ProjectObject::class.java).findAll()

            results.forEach {projectObject ->
                val project = Project(projectObject.uid, projectObject.title, projectObject.description, projectObject.video)
                projects.add(project)
            }

            projects.reverse()

            observer.onNext(projects)

            realm.close()
        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
    }
}