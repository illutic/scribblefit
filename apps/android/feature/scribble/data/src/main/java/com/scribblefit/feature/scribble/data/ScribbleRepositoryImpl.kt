package com.scribblefit.feature.scribble.data

import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.entity.scribble.toDomain
import com.scribblefit.core.database.entity.scribble.toEntity
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
    private val coroutineDispatcher: CoroutineDispatcher,
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

    override fun getScribble(scribbleId: Long): Flow<Scribble> =
        scribbleDao.getScribbleWithExercises(scribbleId)
            .map { it?.toDomain() ?: error("Scribble not found") }
            .flowOn(coroutineDispatcher)

    override fun getScribblesByDate(date: Long): Flow<List<Scribble>> =
        scribbleDao.getScribblesWithExercisesForDate(date)
            .map { it.map { scribbleWithExercises -> scribbleWithExercises.toDomain() } }
            .flowOn(coroutineDispatcher)

}
