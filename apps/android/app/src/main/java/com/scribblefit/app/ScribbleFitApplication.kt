package com.scribblefit.app

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.auth
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScribbleFitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)

        Log.d("ScribbleFitAuth", "Attempting anonymous sign-in...")
        // Sign in anonymously to provide auth context for Gemini / App Check
        Firebase.auth.signInAnonymously()
            .addOnSuccessListener {
                Log.d("ScribbleFitAuth", "Anonymous sign-in successful: ${it.user?.uid}")
            }
            .addOnFailureListener {
                Log.e("ScribbleFitAuth", "Anonymous sign-in failed", it)
            }

        Firebase.appCheck.installAppCheckProviderFactory(
            if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )
    }
}
