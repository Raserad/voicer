package com.raserad.voicer.presentation.ui.editor.entities

data class SoundRecordViewData(
    val path: String,
    val start: Long,
    val end: Long,
    var total: Long,
    var isEnabled: Boolean
)