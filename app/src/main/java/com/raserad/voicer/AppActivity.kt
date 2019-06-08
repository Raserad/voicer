package com.raserad.voicer

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.raserad.voicer.di.AppDI
import com.raserad.voicer.services.RemoveKeeperService
import io.realm.Realm
import io.realm.RealmConfiguration
import android.content.Intent



class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppDI.start(this)
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(R.style.AppTheme, true)
        return theme
    }

    override fun onStop() {
        super.onStop()
        if(isFinishing) {
            AppDI.finish()
            val intent = Intent(this, RemoveKeeperService::class.java)
            startService(intent)
        }
    }
}
