package com.example

import android.app.Application
import android.content.ComponentCallbacks2

class CemaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code here
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Handle memory trim requests from the OS to release resources proactively
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            // App is in background, release non-critical caches
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // Handle extreme low memory situations
    }
}
