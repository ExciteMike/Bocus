package com.excitemike.bocus.data

import kotlinx.coroutines.flow.Flow

interface BocusRepository {
    fun getAllAlarmsStream(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm:Alarm)
    suspend fun deleteAlarm(alarm:Alarm)
    suspend fun updateAlarm(alarm:Alarm)
    suspend fun getAlarm(id:Int): Flow<Alarm?>
    suspend fun getAllAlarmsRaw(): List<Alarm>
}