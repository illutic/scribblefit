package com.scribblefit.feature.profile.data.repository

import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.model.SystemConfig
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.profile.domain.model.*
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsRepositoryImplTest {

    private lateinit var configRepository: ConfigRepository
    private lateinit var database: ScribbleFitDatabase
    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setup() {
        configRepository = mockk(relaxed = true)
        database = mockk(relaxed = true)
        repository = SettingsRepositoryImpl(configRepository, database)
    }

    @Test
    fun `getSettings maps domain config to AppSettings correctly`() = runTest {
        // Given
        val config = SystemConfig(
            promptVersion = "1.0",
            promptText = "",
            exerciseVersion = "0.0.0",
            preferredLlmProvider = LLMProvider.OPENAI,
            parsingMode = "PERSONAL",
            weightUnit = "KG",
            themePreference = "DARK",
            updatedAt = 1000L
        )
        every { configRepository.getConfig() } returns flowOf(config)

        // When
        val settings = repository.getSettings().first()

        // Then
        assertEquals(ParsingMode.PERSONAL, settings.parsingMode)
        assertEquals(LLMProvider.OPENAI, settings.aiProvider)
        assertEquals(WeightUnit.KG, settings.weightUnit)
        assertEquals(ThemePreference.DARK, settings.themePreference)
    }

    @Test
    fun `updateSettings updates configuration in repository`() = runTest {
        // Given
        val settings = AppSettings(
            parsingMode = ParsingMode.CLOUD,
            aiProvider = LLMProvider.GEMINI,
            weightUnit = WeightUnit.LBS,
            themePreference = ThemePreference.LIGHT
        )
        every { configRepository.getConfig() } returns flowOf(null)

        // When
        repository.updateSettings(settings)

        // Then
        coVerify { 
            configRepository.updateConfig(match { 
                it.parsingMode == "CLOUD" && 
                it.preferredLlmProvider == LLMProvider.GEMINI &&
                it.weightUnit == "LBS"
            }) 
        }
    }
}
