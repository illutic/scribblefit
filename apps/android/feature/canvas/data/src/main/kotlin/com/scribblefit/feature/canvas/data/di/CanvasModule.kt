package com.scribblefit.feature.canvas.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsFailedUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsPendingUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleWithWorkoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object CanvasModule {
    @Provides
    fun provideParsePendingScribblesUseCase(
        getPendingScribblesByDateUseCase: GetPendingScribblesByDateUseCase,
        updateScribbleWithWorkoutUseCase: UpdateScribbleWithWorkoutUseCase,
        updateScribbleAsFailedUseCase: UpdateScribbleAsFailedUseCase,
        updateScribbleAsPendingUseCase: UpdateScribbleAsPendingUseCase,
        llmEngine: LLMEngine,
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
    ): ParsePendingScribblesUseCase =
        ParsePendingScribblesUseCase(
            getPendingScribblesByDateUseCase = getPendingScribblesByDateUseCase,
            updateScribbleWithWorkoutUseCase = updateScribbleWithWorkoutUseCase,
            updateScribbleAsFailedUseCase = updateScribbleAsFailedUseCase,
            updateScribbleAsPendingUseCase = updateScribbleAsPendingUseCase,
            llmEngine = llmEngine,
            coroutineDispatcher = coroutineDispatcherProvider.io(),
        )
}
