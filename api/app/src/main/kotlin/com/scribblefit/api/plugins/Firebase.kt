package com.scribblefit.api.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import org.slf4j.LoggerFactory

fun Application.configureFirebase() {
    val logger = LoggerFactory.getLogger("Firebase")
    val useFirebase = environment.config.propertyOrNull("scribblefit.firebase.enabled")?.getString()?.toBoolean() ?: false
    
    if (!useFirebase) {
        logger.info("Firebase is disabled by configuration.")
        return
    }

    try {
        if (FirebaseApp.getApps().isEmpty()) {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()
            FirebaseApp.initializeApp(options)
            logger.info("Firebase Admin SDK initialized successfully.")
        }
    } catch (e: Exception) {
        logger.error("Failed to initialize Firebase Admin SDK: ${e.message}")
        // We don't fatalError here so the app can still run with local config if Firebase fails
    }
}
