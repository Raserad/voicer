package com.raserad.voicer.data.project.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProjectRemoveObject: RealmObject() {
    @PrimaryKey var uid = ""
}