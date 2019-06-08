package com.raserad.voicer.data.video.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VideoSoundObject: RealmObject() {
    @PrimaryKey var uid = ""
    var path = ""
}