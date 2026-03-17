package com.scribblefit.feature.scribble.data

import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.dao.ScribbleTrackerDao
import com.scribblefit.core.database.entity.scribble.ScribbleExercise
import com.scribblefit.core.database.entity.scribble.ScribbleStatus
import com.scribblefit.core.database.mapper.toDomain
import com.scribblefit.core.database.mapper.toEntity
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of ScribbleRepository.
 */
internal class ScribbleRepositoryImpl(
    private val scribbleDao: ScribbleDao,
    private val scribbleTrackerDao: ScribbleTrackerDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : ScribbleRepository {
    override suspend fun insertScribble(scribble: Scribble): Long =
        withContext(coroutineDispatcher) {
            scribbleDao.insertScribble(scribble.toEntity())
        }

    override suspend fun updateScribble(scribble: Scribble) = withContext(coroutineDispatcher) {
        scribbleDao.updateScribble(scribble.toEntity())
    }

    override suspend fun deleteScribble(scribbleId: Long) = withContext(coroutineDispatcher) {
        scribbleDao.deleteScribble(scribbleId)
    }

    override suspend fun addExerciseToScribble(
        scribbleId: Long,
        workoutExerciseId: Long
    ): Long = withContext(coroutineDispatcher) {
        val entity = ScribbleExercise(
            scribbleId = scribbleId,
            workoutExerciseId = workoutExerciseId
        )
        scribbleTrackerDao.insertScribbleExercise(entity)
    }

    override fun getScribble(scribbleId: Long): Flow<Scribble> =
        scribbleDao
            .getScribbleById(scribbleId)
            .flowOn(coroutineDispatcher)
            .map { it.toDomain() }

    override fun getScribbleWithExercises(scribbleId: Long): Flow<Scribble> =
        scribbleTrackerDao
            .getScribbleWithExercises(scribbleId)
            .flowOn(coroutineDispatcher)
            .map { it.toDomain() }

    override fun getPendingScribblesByDate(date: Long): Flow<List<Scribble>> =
        scribbleDao
            .getScribblesByStatusAndDate(ScribbleStatus.PENDING.name, date)
            .flowOn(coroutineDispatcher)
            .map { list -> list.map { it.toDomain() } }

    override fun getScribblesByDate(date: Long): Flow<List<Scribble>> =
        scribbleDao
            .getAllScribblesByDate(date)
            .flowOn(coroutineDispatcher)
            .map { list -> list.map { it.toDomain() } }
}
