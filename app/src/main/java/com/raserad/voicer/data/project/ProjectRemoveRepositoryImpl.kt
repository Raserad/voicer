package com.raserad.voicer.data.project

import com.raserad.voicer.data.project.entities.ProjectObject
import com.raserad.voicer.data.project.entities.ProjectRemoveObject
import com.raserad.voicer.data.sound.entities.SoundRecordObject
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.remove.ProjectRemoveRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.io.File

class ProjectRemoveRepositoryImpl: ProjectRemoveRepository {

    override fun markToRemove(project: Project): Observable<Boolean> {
        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {db ->
                val removeObject = ProjectRemoveObject()
                removeObject.uid = project.uid
                db.insert(removeObject)
            }

            realm.close()
            observer.onNext(true)
        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
    }

    override fun unmarkMarked(): Observable<Boolean> {
        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {db ->
                val results = db.where(ProjectRemoveObject::class.java).findAll()
                results.deleteAllFromRealm()
            }

            realm.close()
            observer.onNext(true)
        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
    }

    override fun removeMarked(): Observable<Boolean> {

        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction {db ->
                val results = db.where(ProjectRemoveObject::class.java).findAll()
                results.forEach {projectRemove ->
                    val projects = db.where(ProjectObject::class.java).equalTo("uid", projectRemove.uid).findAll()

                    projects.forEach {project ->
                        val videoFile = File(project.video)
                        videoFile.delete()
                        val audioFile = File("${project.video}.aac")
                        audioFile.delete()
                    }

                    projects.deleteAllFromRealm()

                    val records = db.where(SoundRecordObject::class.java).equalTo("uid", projectRemove.uid).findAll()

                    records.forEach { record ->
                        val recordFile = File(record.path)
                        recordFile.delete()
                    }

                    records.deleteAllFromRealm()
                }

                results.deleteAllFromRealm()
            }

            realm.close()
            observer.onNext(true)
        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
    }
}