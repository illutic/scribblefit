package com.scribblefit.api.plugins

import com.scribblefit.api.services.ConfigService
import com.scribblefit.api.services.ConfigServiceImpl
import com.scribblefit.api.services.FirebaseConfigServiceImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    val appModule = module {
        single<ConfigService> {
            val useFirebase = environment.config.propertyOrNull("scribblefit.firebase.enabled")?.getString()?.toBoolean() ?: false
            if (useFirebase) {
                FirebaseConfigServiceImpl()
            } else {
                ConfigServiceImpl(environment.config)
            }
        }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
