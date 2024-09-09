package com.amorgens.general.utils

import android.util.Log
import io.sentry.Sentry
import io.sentry.SentryLevel

object CLog {
    fun debug(tag: String, message: String) {
        Log.d(tag, message)
       // Sentry.captureMessage("[$tag] $message", SentryLevel.DEBUG)
    }

    fun info(tag: String, message: String) {
        Log.d(tag, message)
        Sentry.captureMessage("[$tag] $message", SentryLevel.INFO)
    }

    fun error(tag: String, message: String) {
        Log.e(tag, message)
        Sentry.captureMessage("[$tag] $message", SentryLevel.ERROR)

    }

}

fun clog(tag: String, message: String){
    Sentry.captureMessage("[$tag] $message", SentryLevel.ERROR)
    Sentry.captureException(RuntimeException("Wiggle room"))
}