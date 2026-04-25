package com.scribblefit.feature.sets.data

import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.entity.set.toEntity
import com.scribblefit.core.model.Set
import com.scribblefit.feature.sets.domain.SetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class SetRepositoryImpl(
    private val setDao: SetDao,
    private val coroutineDispatcher: CoroutineDispatcher
) : SetRepository {
    override suspend fun addSet(
        exerciseId: Long,
        set: Set
    ): Long = withContext(coroutineDispatcher) {
        setDao.insertSet(set.toEntity(exerciseId))
    }

    override suspend fun updateSetReps(setId: Long, reps: Int) = withContext(coroutineDispatcher) {
        val setEntity = setDao.getSetById(setId) ?: error("Set with id $setId not found")
        setDao.updateSet(
            setEntity.copy(
                reps = reps
            )
        )
    }

    override suspend fun updateSetWeight(setId: Long, weight: Float?) =
        withContext(coroutineDispatcher) {
            val setEntity = setDao.getSetById(setId) ?: error("Set with id $setId not found")
            setDao.updateSet(
                setEntity.copy(
                    weight = weight
                )
            )
        }

    override suspend fun deleteSet(setId: Long) = withContext(coroutineDispatcher) {
        setDao.deleteSet(setId)
    }
}
