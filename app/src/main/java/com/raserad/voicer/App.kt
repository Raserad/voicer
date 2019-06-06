package com.raserad.voicer

import android.app.Application
import android.content.Context
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import io.realm.Realm
import io.realm.Realm.setDefaultConfiguration
import io.realm.RealmConfiguration

class App: Application() {

    override fun onCreate() {
        instance = this
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder().name("voicer.realm").build()
        setDefaultConfiguration(config)

        FFmpeg.getInstance(this).loadBinary(null)
    }

    companion object {
        private var instance: App? = null

        fun getContext(): Context? {
            return instance
        }
    }
}