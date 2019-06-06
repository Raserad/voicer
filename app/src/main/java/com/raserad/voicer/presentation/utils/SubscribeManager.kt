package com.raserad.voicer.presentation.utils

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.lang.StringBuilder

class SubscribeManager {

    private val disposables: MutableMap<String, Disposable> = HashMap()

    fun <T> subscribe(observable: Observable<T>, identifier: String = "") {
        var key = identifier
        if(key.isEmpty()) {
            key = generateKey(40)
        }
        disposables[key]?.dispose()
        disposables[key] = observable.subscribe()
    }

    fun unsubscribe(identifier: String) {
        disposables[identifier]?.dispose()
    }

    fun unsubscribeAll() {
        disposables.forEach { it.value.dispose() }
    }

    private fun generateKey(length: Int): String {
        val ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val builder = StringBuilder()
        var count = length
        while (count-- != 0) {
            val character = (Math.random()*ALPHA_NUMERIC_STRING.length).toInt()
            builder.append(ALPHA_NUMERIC_STRING[character])
        }
        return builder.toString()
    }
}