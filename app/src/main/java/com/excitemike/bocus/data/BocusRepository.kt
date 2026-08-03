package com.excitemike.bocus.data

import kotlinx.coroutines.flow.Flow

// TODO: what's the point of this? use the daos directly
interface BocusRepository {
    fun getAllAlarmsStream(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm: Alarm): Int
    suspend fun deleteAlarm(alarmId: Long)
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun getAlarm(id: Long): Alarm?
    suspend fun getAllAlarmsRaw(): List<Alarm>
}