package com.excitemike.bocus.data

import kotlinx.coroutines.flow.Flow

class OfflineBocusRepository(private val alarmDao: AlarmDao) : BocusRepository {
    override fun getAllAlarmsStream(): Flow<List<Alarm>> = alarmDao.getAllAlarms()
    override suspend fun getAlarm(id: Int): Alarm? = alarmDao.getAlarm(id)
    override suspend fun insertAlarm(alarm: Alarm): Int = alarmDao.insert(alarm).toInt()
    override suspend fun deleteAlarm(alarmId: Int) = alarmDao.delete(AlarmId(alarmId))
    override suspend fun updateAlarm(alarm: Alarm) = alarmDao.update(alarm)
    override suspend fun getAllAlarmsRaw(): List<Alarm> = alarmDao.getAllAlarmsRaw()
}
