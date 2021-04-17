package dev.sanson.tick.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class TickApplication : Application() {
    init {
        context = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
