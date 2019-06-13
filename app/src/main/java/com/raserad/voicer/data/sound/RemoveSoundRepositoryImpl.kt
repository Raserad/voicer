package com.raserad.voicer.data.sound

import com.raserad.voicer.data.sound.entities.SoundRecordObject
import com.raserad.voicer.data.sound.entities.SoundRecordRemoveObject
import com.raserad.voicer.data.sound.entities.SoundRecordRemoveTempObject
import com.raserad.voicer.data.sound.entities.SoundRecordTempObject
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.sound.remove.RemoveSoundRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.io.File

class RemoveSoundRepositoryImpl: RemoveSoundRepository {
    override fun markToRemove(project: Project, soundRecord: SoundRecord): Observable<Boolean> {
        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction {db ->
                val removeObject = SoundRecordRemoveTempObject()
                removeObject.uid = project.uid
                removeObject.id = soundRecord.id
                db.copyToRealmOrUpdate(removeObject)
            }

            realm.close()
            observer.onNext(true)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }

    override fun unmarkMarked(): Observable<Boolean> {
        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction {db ->
                val results = db.where(SoundRecordRemoveTempObject::class.java).findAll()
                results.deleteAllFromRealm()
            }

            realm.close()
            observer.onNext(true)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    }

    override fun removeMarked(): Observable<Boolean> {
        return Observable.create<Boolean> {observer ->
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction {db ->
                val removeObjects = db.where(SoundRecordRemoveObject::class.java).findAll()

                removeObjects.forEach {recordRemove ->
                    val records = db.where(SoundRecordObject::class.java).equalTo("uid", recordRemove.uid).and().equalTo("id", recordRemove.id).findAll()

                    records.forEach {record ->
                        val soundFile = File(record.path)
                        soundFile.delete()
                    }

                    records.deleteAllFromRealm()
                }

                removeObjects.deleteAllFromRealm()

                db.where(SoundRecordTempObject::class.java).findAll().deleteAllFromRealm()
            }

            realm.close()
            observer.onNext(true)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
}