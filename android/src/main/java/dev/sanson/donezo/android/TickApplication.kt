package dev.sanson.donezo.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class DonezoApplication : Application() {
    init {
        context = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
