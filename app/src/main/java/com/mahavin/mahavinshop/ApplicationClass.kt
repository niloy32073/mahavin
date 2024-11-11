package com.mahavin.mahavinshop

import android.app.Application
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val ONESIGNAL_APP_ID: String = "c75e18e3-0a46-4471-a94c-6171bb1f4fac"
class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // OneSignal Initialization
        OneSignal.initWithContext(this)

        OneSignal.setAppId(ONESIGNAL_APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.promptForPushNotifications()
        }
    }
}