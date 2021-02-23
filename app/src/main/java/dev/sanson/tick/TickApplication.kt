package dev.sanson.tick

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