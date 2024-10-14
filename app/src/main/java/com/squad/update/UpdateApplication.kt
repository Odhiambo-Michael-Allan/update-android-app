package com.squad.update

import android.app.Application
import com.squad.update.sync.initializers.Sync
import dagger.hilt.android.HiltAndroidApp

/**
 * [Application] class for Update
 */
@HiltAndroidApp
class UpdateApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Sync; the system responsible for keeping data in the app up to date.
        Sync.initialize( context = this )
    }
}