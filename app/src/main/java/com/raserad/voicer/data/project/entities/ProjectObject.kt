package com.raserad.voicer.data.project.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProjectObject: RealmObject() {
    @PrimaryKey var uid = ""
    var title = ""
    var description = ""

    var video = ""
    var preview = ""


}