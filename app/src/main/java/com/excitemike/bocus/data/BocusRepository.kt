package com.excitemike.bocus.data

import kotlinx.coroutines.flow.Flow

interface BocusRepository {
    fun getAllAlarmsStream(): Flow<List<Alarm>>
    suspend fun insertAlarm(alarm:Alarm): Int
    suspend fun deleteAlarm(alarmId:Int)
    suspend fun updateAlarm(alarm:Alarm)
    suspend fun getAlarm(id:Int): Flow<Alarm?>
    suspend fun getAllAlarmsRaw(): List<Alarm>
}