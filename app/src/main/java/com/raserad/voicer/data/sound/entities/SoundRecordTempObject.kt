package com.raserad.voicer.data.sound.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SoundRecordTempObject: RealmObject() {
    @PrimaryKey var id = 0
    var uid = ""
    var path = ""
    var start = 0L
    var end = 0L
    var total = 0L
    var isEnabled = false
}