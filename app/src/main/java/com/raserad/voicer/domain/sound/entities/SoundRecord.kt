package com.raserad.voicer.domain.sound.entities

data class SoundRecord(
    var id: Int,
    var path: String,
    var start: Long,
    var end: Long,
    var total: Long,
    var isEnabled: Boolean
)