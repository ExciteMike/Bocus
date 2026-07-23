package com.excitemike.bocus.data

import kotlinx.coroutines.flow.Flow

class OfflineBocusRepository(private val alarmDao: AlarmDao) : BocusRepository {
    override fun getAllAlarmsStream(): Flow<List<Alarm>> = alarmDao.getAllAlarms()
    override suspend fun getAlarm(id: Int): Flow<Alarm?> = alarmDao.getAlarm(id)
    override suspend fun insertAlarm(alarm: Alarm) = alarmDao.insert(alarm)
    override suspend fun deleteAlarm(alarm: Alarm) = alarmDao.delete(alarm)
    override suspend fun updateAlarm(alarm: Alarm) = alarmDao.update(alarm)
}
