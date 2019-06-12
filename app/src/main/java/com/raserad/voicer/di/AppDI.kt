package com.raserad.voicer.di

import android.app.Activity
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.utils.RouterImpl

object AppDI {

    private var repositoryDI: RepositoryDI? = null
    private var router: Router? = null

    fun start(activity: Activity) {
        if (router != null) {
            (router as RouterImpl).activity = activity
            return
        }

        router = RouterImpl(activity)
        router?.start()
    }

    fun finish() {
        router = null
    }

    fun getRouter(): Router {
        return router!!
    }

    fun getPresenterDI(): PresenterDI {
        if(repositoryDI == null) {
            repositoryDI = RepositoryDI()
        }
        val interactorDI = InteractorDI(repositoryDI!!)
        return PresenterDI(interactorDI)
    }
}