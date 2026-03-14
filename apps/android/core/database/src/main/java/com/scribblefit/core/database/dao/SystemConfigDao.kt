package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scribblefit.core.database.entity.config.SystemConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemConfigDao {
    @Query("SELECT * FROM system_config WHERE id = :id")
    fun getSystemConfig(id: Long = 0): Flow<SystemConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSystem(system: SystemConfig): Long

    @Query("DELETE FROM system_config")
    suspend fun clearSystemConfig()
}
