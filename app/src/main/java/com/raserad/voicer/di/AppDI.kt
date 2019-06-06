package com.raserad.voicer.di

import android.app.Activity
import android.util.Log
import com.raserad.voicer.presentation.Router

object AppDI {

    private var router: Router? = null

    fun start(activity: Activity) {
        if (router != null) {
            (router as RouterDI).activity = activity
            return
        }
        Log.d("STARTING", "okay")
        val repositoryDI = RepositoryDI()
        val interactorDI = InteractorDI(repositoryDI)
        val presenterDI = PresenterDI(interactorDI)
        router = RouterDI(activity, presenterDI)

        router?.start()
    }

    fun finish() {
        router = null
    }
}