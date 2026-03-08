package com.scribblefit.core.database

import com.scribblefit.core.database.model.SyncStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun fromSyncStatus() {
        assertEquals("PENDING", converters.fromSyncStatus(SyncStatus.PENDING))
        assertEquals("COMPLETED", converters.fromSyncStatus(SyncStatus.COMPLETED))
    }

    @Test
    fun toSyncStatus() {
        assertEquals(SyncStatus.PENDING, converters.toSyncStatus("PENDING"))
        assertEquals(SyncStatus.COMPLETED, converters.toSyncStatus("COMPLETED"))
    }

    @Test
    fun stringListConverters() {
        val list = listOf("a", "b", "c")
        val csv = converters.fromStringList(list)
        assertEquals("a,b,c", csv)
        assertEquals(list, converters.toStringList(csv))
    }
}
