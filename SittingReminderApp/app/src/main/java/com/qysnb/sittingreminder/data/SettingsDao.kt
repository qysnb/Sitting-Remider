package com.qysnb.sittingreminder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings WHERE id = 1")
    fun observeSettings(): Flow<Settings?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettings(): Settings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: Settings)
}
