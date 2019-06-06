package com.raserad.voicer.data.sound

import com.raserad.voicer.data.sound.entities.SoundRecordObject
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.SoundRepository
import com.raserad.voicer.domain.sound.entities.SoundRecord
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

class SoundRepositoryImpl: SoundRepository {

    override fun getList(project: Project): Observable<MutableList<SoundRecord>> {
        return Observable.create<MutableList<SoundRecord>> {observer ->
            val records: MutableList<SoundRecord> = ArrayList()

            val realm = Realm.getDefaultInstance()

            val results = realm.where(SoundRecordObject::class.java).equalTo("uid", project.uid).findAll()

            results.forEach {recordObject ->
                val record = SoundRecord(recordObject.id, recordObject.path, recordObject.start, recordObject.end, recordObject.isEnabled)
                records.add(record)
            }

            records.reverse()

            observer.onNext(records)

            realm.close()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun addToProject(project: Project, soundRecord: SoundRecord) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {db ->
            val recordObject = SoundRecordObject()
            recordObject.uid = project.uid
            recordObject.id = soundRecord.id
            recordObject.path = soundRecord.path
            recordObject.start = soundRecord.start
            recordObject.end = soundRecord.end
            recordObject.isEnabled = soundRecord.isEnabled
            db.insert(recordObject)
        }

        realm.close()
    }

    override fun enableInProject(project: Project, soundRecord: SoundRecord, isEnabled: Boolean) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {db ->
            val recordObject = SoundRecordObject()
            recordObject.uid = project.uid
            recordObject.id = soundRecord.id
            recordObject.path = soundRecord.path
            recordObject.start = soundRecord.start
            recordObject.end = soundRecord.end
            recordObject.isEnabled = isEnabled
            db.copyToRealmOrUpdate(recordObject)
        }

        realm.close()
    }
}