package com.raserad.voicer.data.video.entites

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ReleaseVideoObject: RealmObject() {
    @PrimaryKey var uid = ""
    var path = ""
}