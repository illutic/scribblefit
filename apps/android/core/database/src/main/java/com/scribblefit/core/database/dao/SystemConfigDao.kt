package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.entity.SystemConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemConfigDao {
    @Query("SELECT * FROM System_Config WHERE id = 'config' LIMIT 1")
    fun observe(): Flow<SystemConfigEntity?>

    @Query("SELECT * FROM System_Config WHERE id = 'config' LIMIT 1")
    suspend fun get(): SystemConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(config: SystemConfigEntity)

    @Query("DELETE FROM System_Config")
    suspend fun deleteAll()
}
