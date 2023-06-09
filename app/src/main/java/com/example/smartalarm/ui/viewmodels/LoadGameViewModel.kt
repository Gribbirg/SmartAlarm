package com.example.smartalarm.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.data.db.AlarmsDB
import com.example.smartalarm.data.repositories.AlarmDbRepository
import kotlinx.coroutines.launch

class LoadGameViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmDbRepository = AlarmDbRepository(
        AlarmsDB.getInstance(getApplication())?.alarmsDao()!!
    )

    var currentGame: MutableLiveData<List<Int>> = MutableLiveData()
    var path = MutableLiveData<String>()

    fun getAlarm(alarmId: Long) {
        if (alarmId.toInt() == -1) return

        viewModelScope.launch {
            val alarm = alarmDbRepository.getAlarmWithGames(alarmId)
            path.postValue(alarm.ringtonePath)
            val gamesActive = ArrayList<Int>()
            for (i in alarm.gamesList.indices) {
                if (alarm.gamesList[i] != 0)
                    gamesActive.add(i)
            }
            if (gamesActive.isEmpty()) {
                currentGame.postValue(listOf())
            } else {
                val iGame = (gamesActive.indices).random()
                currentGame.postValue(
                    listOf(
                        gamesActive[iGame] + 1,
                        alarm.gamesList[gamesActive[iGame]]
                    )
                )
                Log.i("game", "Choose game: ${currentGame.value}")
            }
        }
    }

}