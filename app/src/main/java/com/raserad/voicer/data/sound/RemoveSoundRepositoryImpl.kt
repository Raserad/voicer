package com.raserad.voicer.data.sound

import com.raserad.voicer.data.sound.entities.SoundRecordObject
import com.raserad.voicer.data.sound.entities.SoundRecordRemoveObject
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.sound.remove.RemoveSoundRepository
import io.realm.Realm
import java.io.File

class RemoveSoundRepositoryImpl: RemoveSoundRepository {
    override fun markToRemove(project: Project, soundRecord: SoundRecord) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {db ->
            val removeObject = SoundRecordRemoveObject()
            removeObject.uid = project.uid
            removeObject.id = soundRecord.id
            db.insert(removeObject)
        }

        realm.close()
    }

    override fun unmarkMarked() {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {db ->
            val results = db.where(SoundRecordRemoveObject::class.java).findAll()
            results.deleteAllFromRealm()
        }

        realm.close()
    }

    override fun removeMarked() {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {db ->
            val results = db.where(SoundRecordRemoveObject::class.java).findAll()
            results.forEach {recordRemove ->
                val records = db.where(SoundRecordObject::class.java).equalTo("uid", recordRemove.uid).and().equalTo("id", recordRemove.id).findAll()

                records.forEach {record ->
                    val soundFile = File(record.path)
                    soundFile.delete()
                }

                records.deleteAllFromRealm()
            }

            results.deleteAllFromRealm()
        }

        realm.close()
    }
}