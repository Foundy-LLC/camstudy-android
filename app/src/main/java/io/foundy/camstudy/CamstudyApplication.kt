package io.foundy.camstudy

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.getstream.log.android.AndroidStreamLogger
import org.mediasoup.droid.MediasoupClient

@HiltAndroidApp
class CamstudyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidStreamLogger.installOnDebuggableApp(this)
        MediasoupClient.initialize(applicationContext)
    }
}
