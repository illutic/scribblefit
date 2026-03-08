package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.database.dao.ExerciseDictionaryDao
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.model.ExerciseDictionaryEntity
import com.scribblefit.core.database.model.SystemConfigEntity
import com.scribblefit.core.network.ScribbleFitApi
import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.data.mapper.toEntity
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.model.SystemConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepositoryImpl @Inject constructor(
    private val api: ScribbleFitApi,
    private val systemConfigDao: SystemConfigDao,
    private val exerciseDictionaryDao: ExerciseDictionaryDao
) : ConfigRepository {

    override fun getConfig(): Flow<SystemConfig?> {
        return systemConfigDao.getConfig().map { it?.toDomain() }
    }

    override suspend fun updateConfig(config: SystemConfig) {
        systemConfigDao.upsertConfig(config.toEntity())
    }

    override suspend fun syncMetadata(): Result<Unit> = runCatching {
        val metadata = api.getMetadata()
        val currentConfig = systemConfigDao.getConfig().firstOrNull()

        if (currentConfig == null || currentConfig.promptVersion != metadata.promptVersion) {
            val promptConfig = api.getPromptConfig()
            val newConfig = (currentConfig ?: SystemConfigEntity(
                promptVersion = promptConfig.version,
                promptText = promptConfig.prompt,
                updatedAt = System.currentTimeMillis()
            )).copy(
                promptVersion = promptConfig.version,
                promptText = promptConfig.prompt,
                updatedAt = System.currentTimeMillis()
            )
            systemConfigDao.upsertConfig(newConfig)
        }
    }

    override suspend fun syncExercises(): Result<Unit> = runCatching {
        val metadata = api.getMetadata()
        val currentConfig = systemConfigDao.getConfig().firstOrNull()

        if (currentConfig == null || currentConfig.exerciseVersion != metadata.exerciseVersion) {
            val response = api.getExercises()
            val entities = response.exercises.map { dto ->
                ExerciseDictionaryEntity(
                    id = dto.id,
                    canonicalName = dto.canonicalName,
                    muscleGroup = dto.muscleGroup,
                    aliases = dto.aliases
                )
            }
            exerciseDictionaryDao.deleteAll()
            exerciseDictionaryDao.upsertExercises(entities)

            // Update exercise version in config
            val updatedConfig = (currentConfig ?: SystemConfigEntity(
                promptVersion = "0.0.0",
                promptText = "",
                updatedAt = System.currentTimeMillis()
            )).copy(
                exerciseVersion = metadata.exerciseVersion,
                updatedAt = System.currentTimeMillis()
            )
            systemConfigDao.upsertConfig(updatedConfig)
        }
    }
}
