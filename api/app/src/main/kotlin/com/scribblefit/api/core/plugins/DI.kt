package com.scribblefit.api.core.plugins

import com.scribblefit.api.features.config.ConfigService
import com.scribblefit.api.features.config.ConfigServiceImpl
import com.scribblefit.api.features.config.FirebaseConfigServiceImpl
import com.scribblefit.api.features.exercises.ExerciseService
import com.scribblefit.api.features.exercises.ExerciseServiceImpl
import com.scribblefit.api.features.parser.AiParserService
import com.scribblefit.api.features.parser.OpenAiParserService
import com.scribblefit.api.features.telemetry.TelemetryService
import com.scribblefit.api.features.telemetry.TelemetryServiceImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    val appModule = module {
        single<ExerciseService> {
            ExerciseServiceImpl()
        }

        single<ConfigService> {
            val useFirebase = environment.config.propertyOrNull("scribblefit.firebase.enabled")?.getString()?.toBoolean() ?: false
            if (useFirebase) {
                FirebaseConfigServiceImpl()
            } else {
                ConfigServiceImpl(environment.config, get())
            }
        }
        
        single<AiParserService> {
            val apiKey = System.getenv("OPENAI_API_KEY") ?: "missing-key"
            OpenAiParserService(get(), apiKey)
        }

        single<TelemetryService> {
            TelemetryServiceImpl()
        }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
