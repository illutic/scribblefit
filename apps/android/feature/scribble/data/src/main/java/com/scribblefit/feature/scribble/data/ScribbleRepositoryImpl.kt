package com.scribblefit.feature.scribble.data

import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.entity.scribble.ScribbleEntity
import com.scribblefit.core.database.entity.scribble.ScribbleStatus
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of ScribbleRepository.
 */
internal class ScribbleRepositoryImpl(
    private val scribbleDao: ScribbleDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : ScribbleRepository {

    override suspend fun saveRawScribble(text: String): Long = withContext(coroutineDispatcher) {
        val entity = ScribbleEntity(
            rawText = text,
            status = ScribbleStatus.RAW.name
        )
        scribbleDao.insertScribble(entity)
    }

    override suspend fun updateScribbleWithParsedData(scribbleId: Long, parsedJson: String) =
        withContext(coroutineDispatcher) {
            val existing = scribbleDao.getScribbleById(scribbleId).first()
            scribbleDao.updateScribble(
                existing.copy(
                    parsedJson = parsedJson,
                    status = ScribbleStatus.PARSED.name
                )
            )
        }

    override suspend fun markScribbleCompleted(scribbleId: Long, workoutExerciseId: Long) =
        withContext(coroutineDispatcher) {
            val existing = scribbleDao.getScribbleById(scribbleId).first()
            scribbleDao.updateScribble(
                existing.copy(
                    status = ScribbleStatus.COMPLETED.name,
                    workoutExerciseId = workoutExerciseId
                )
            )
        }

    override suspend fun markScribbleFailed(scribbleId: Long) = withContext(coroutineDispatcher) {
        val existing = scribbleDao.getScribbleById(scribbleId).first()
        scribbleDao.updateScribble(
            existing.copy(status = ScribbleStatus.FAILED.name)
        )
    }

    override fun getScribble(scribbleId: Long): Flow<Scribble> =
        scribbleDao
            .getScribbleById(scribbleId)
            .flowOn(coroutineDispatcher)
            .map { it.toDomain() }

    override fun getPendingScribbles(): Flow<List<Scribble>> =
        scribbleDao
            .getScribblesByStatus(ScribbleStatus.RAW.name)
            .flowOn(coroutineDispatcher)
            .map { list -> list.map { it.toDomain() } }
}
