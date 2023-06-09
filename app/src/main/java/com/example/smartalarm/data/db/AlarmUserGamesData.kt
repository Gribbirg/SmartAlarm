package com.example.smartalarm.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_games_table",
    indices = [androidx.room.Index("id")],
    foreignKeys = [
        ForeignKey(
            entity = AlarmSimpleData::class,
            parentColumns = ["id"],
            childColumns = ["alarm_id"]
        ),
        ForeignKey(
            entity = GameData::class,
            parentColumns = ["id"],
            childColumns = ["game_id"]
        )
    ]
)
data class AlarmUserGamesData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "game_id")
    var idGame: Int,

    @ColumnInfo(name = "alarm_id")
    var idAlarm: Long,

    @ColumnInfo(name = "difficulty")
    var difficulty: Int = 1,
)