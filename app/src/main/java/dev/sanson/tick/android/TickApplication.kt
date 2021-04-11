package dev.sanson.tick.android

import android.app.Application
import android.content.Context

class TickApplication : Application() {
  init {
    context = this
  }

  companion object {
    lateinit var context: Context
  }
}