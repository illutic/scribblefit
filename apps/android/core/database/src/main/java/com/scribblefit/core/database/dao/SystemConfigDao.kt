package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.scribblefit.core.database.model.SystemConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemConfigDao {
    @Upsert
    suspend fun upsertConfig(config: SystemConfigEntity)

    @Query("SELECT * FROM System_Config WHERE id = :id")
    fun getConfig(id: String = "config"): Flow<SystemConfigEntity?>

    @Query("DELETE FROM System_Config")
    suspend fun deleteAll()
}
