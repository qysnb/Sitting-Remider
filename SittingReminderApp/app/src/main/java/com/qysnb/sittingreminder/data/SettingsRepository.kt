package com.qysnb.sittingreminder.data

import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dao: SettingsDao) {

    fun observeSettings(): Flow<Settings?> = dao.observeSettings()

    suspend fun getSettings(): Settings = dao.getSettings() ?: Settings()

    suspend fun updateSettings(settings: Settings) = dao.upsertSettings(settings)
}
