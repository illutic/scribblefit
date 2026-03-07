package com.scribblefit.feature.profile.data.repository

import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.model.SystemConfigEntity
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

    private lateinit var systemConfigDao: SystemConfigDao
    private lateinit var database: ScribbleFitDatabase
    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setup() {
        systemConfigDao = mockk(relaxed = true)
        database = mockk(relaxed = true)
        repository = SettingsRepositoryImpl(systemConfigDao, database)
    }

    @Test
    fun `getSettings maps entity to AppSettings correctly`() = runTest {
        // Given
        val entity = SystemConfigEntity(
            promptVersion = "1.0",
            promptText = "",
            preferredLlmProvider = LLMProvider.OPENAI,
            parsingMode = "PERSONAL",
            weightUnit = "KG",
            themePreference = "DARK",
            updatedAt = 1000L
        )
        every { systemConfigDao.getConfig() } returns flowOf(entity)

        // When
        val settings = repository.getSettings().first()

        // Then
        assertEquals(ParsingMode.PERSONAL, settings.parsingMode)
        assertEquals(LLMProvider.OPENAI, settings.aiProvider)
        assertEquals(WeightUnit.KG, settings.weightUnit)
        assertEquals(ThemePreference.DARK, settings.themePreference)
    }

    @Test
    fun `updateSettings upserts new configuration`() = runTest {
        // Given
        val settings = AppSettings(
            parsingMode = ParsingMode.CLOUD,
            aiProvider = LLMProvider.GEMINI,
            weightUnit = WeightUnit.LBS,
            themePreference = ThemePreference.LIGHT
        )

        // When
        repository.updateSettings(settings)

        // Then
        coVerify { 
            systemConfigDao.upsertConfig(match { 
                it.parsingMode == "CLOUD" && 
                it.preferredLlmProvider == LLMProvider.GEMINI &&
                it.weightUnit == "LBS"
            }) 
        }
    }
}
