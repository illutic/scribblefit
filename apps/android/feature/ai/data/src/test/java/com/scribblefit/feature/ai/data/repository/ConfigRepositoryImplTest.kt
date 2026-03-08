package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.model.SystemConfigEntity
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.core.network.model.ConfigResponse
import com.scribblefit.core.network.model.MetadataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConfigRepositoryImplTest {

    private lateinit var api: ScribbleFitApi
    private lateinit var systemConfigDao: SystemConfigDao
    private lateinit var repository: ConfigRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        systemConfigDao = mockk(relaxed = true)
        repository = ConfigRepositoryImpl(api, systemConfigDao)
    }

    @Test
    fun `syncMetadata updates config when versions differ`() = runTest {
        // Given
        val metadata = MetadataResponse("ok", "1.0.0", "1.1.0", "1.0.0")
        val currentConfig =
            SystemConfigEntity(promptVersion = "1.0.0", promptText = "old prompt", updatedAt = 0L)
        val newPrompt = ConfigResponse("1.1.0", "new prompt")

        coEvery { api.getMetadata() } returns metadata
        every { systemConfigDao.getConfig() } returns flowOf(currentConfig)
        coEvery { api.getPromptConfig() } returns newPrompt

        // When
        val result = repository.syncMetadata()

        // Then
        assertTrue(result.isSuccess)
        coVerify { api.getPromptConfig() }
        coVerify {
            systemConfigDao.upsertConfig(match {
                it.promptVersion == "1.1.0" && it.promptText == "new prompt"
            })
        }
    }

    @Test
    fun `syncMetadata skips update when versions match`() = runTest {
        // Given
        val metadata = MetadataResponse("ok", "1.0.0", "1.0.0", "1.0.0")
        val currentConfig =
            SystemConfigEntity(promptVersion = "1.0.0", promptText = "old prompt", updatedAt = 0L)

        coEvery { api.getMetadata() } returns metadata
        every { systemConfigDao.getConfig() } returns flowOf(currentConfig)

        // When
        val result = repository.syncMetadata()

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { api.getPromptConfig() }
        coVerify(exactly = 0) { systemConfigDao.upsertConfig(any()) }
    }
}
