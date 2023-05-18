package com.example.smartalarm.data.db

import androidx.room.*

@Dao
interface AlarmsDao {

    @Insert(entity = GameData::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertGamesData(games: List<GameData>)

    @Insert(entity = AlarmSimpleData::class)
    fun insertNewAlarmData(alarmSimpleData: AlarmSimpleData): Long

    @Insert(entity = AlarmInfoData::class)
    fun insertNewAlarmInfoData(alarmInfoData: AlarmInfoData)

    @Insert(entity = AlarmUserGamesData::class)
    fun insertNewAlarmUserGamesData(alarmUserGamesData: AlarmUserGamesData)

    @Query("SELECT * FROM alarm_table ORDER BY time_hour, time_minute ASC")
    fun getAlarms(): List<AlarmSimpleData>

    @Query("SELECT * FROM alarm_table WHERE day_of_week = :dayOfWeek ORDER BY time_hour, time_minute ASC")
    fun getAlarmsByDay(dayOfWeek: Int): List<AlarmSimpleData>

    @Query("SELECT * FROM alarm_table WHERE id = :id")
    fun getAlarmById(id: Long): AlarmSimpleData

    @Query("SELECT * FROM alarm_table WHERE day_of_week = :dayOfWeek ORDER BY time_hour, time_minute ASC LIMIT 1")
    fun getEarliestAlarm(dayOfWeek: Int): AlarmSimpleData

    @Update(entity = AlarmSimpleData::class)
    fun updateAlarm(alarm: AlarmSimpleData)

    @Delete(entity = AlarmSimpleData::class)
    fun deleteAlarm(alarm: AlarmSimpleData)

    @Query("SELECT * FROM user_games_table WHERE alarm_id = :alarmId ORDER BY game_id")
    fun getGamesByAlarm(alarmId: Long): List<AlarmUserGamesData>

    @Query("SELECT * FROM user_games_table WHERE alarm_id = :alarmId AND game_id = :gameId")
    fun getGamesByAlarmAndGame(alarmId: Long, gameId: Int): AlarmUserGamesData?

    @Query("SELECT * FROM games_table")
    fun getAllGames(): List<GameData>

    @Query("SELECT * FROM games_table WHERE id = :id")
    fun getGameById(id: Int): GameData

    @Query("DELETE FROM user_games_table WHERE alarm_id = :alarmId")
    fun deleteAlarmsGames(alarmId: Long)

    @Insert(entity = RecordsData::class)
    fun insertRecordData(recordsData: RecordsData)

    @Update(entity = GameData::class)
    fun updateGame(gameData: GameData)

    @Query("SELECT * FROM records_table")
    fun getRecords(): List<RecordsData>
}