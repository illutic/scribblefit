package com.scribblefit.feature.ai.data.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class OpenAILLMEngine

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class GeminiLLMEngine

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class LocalLLMEngine




