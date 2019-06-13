package com.raserad.voicer.data.sound.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SoundRecordRemoveTempObject: RealmObject()  {
    @PrimaryKey var id = 0
    var uid = ""
}