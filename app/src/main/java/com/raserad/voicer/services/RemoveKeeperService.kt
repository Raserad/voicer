package com.raserad.voicer.services

import android.app.IntentService
import android.content.Intent
import com.raserad.voicer.di.AppDI

class RemoveKeeperService : IntentService("RemoveKeeperService") {
    override fun onHandleIntent(intent: Intent?) {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        AppDI.getPresenterDI().getRemovedKeeper().keepRemoved {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }
}