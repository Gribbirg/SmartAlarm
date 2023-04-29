package com.example.smartalarm.ui.viewmodels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import com.example.smartalarm.data.db.AlarmSimpleData
import com.example.smartalarm.data.db.AlarmsDB
import com.example.smartalarm.data.repositories.AlarmDbRepository
import com.example.smartalarm.data.repositories.CalendarRepository
import com.example.smartalarm.data.repositories.getDayOfWeekNameVinit
import com.example.smartalarm.data.repositories.getMontNameVinit
import com.example.smartalarm.data.repositories.getTodayNumInWeek
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class AlarmsFragmentViewModel(application: Application) : AndroidViewModel(application) {

    var currentDayOfWeek: Int? = getTodayNumInWeek()
    private val calendarRepository = CalendarRepository()
    var weekCalendarData = calendarRepository.getWeek()

    var alarmsList: MutableLiveData<ArrayList<AlarmSimpleData>> = MutableLiveData()
    var earliestAlarmsList: MutableLiveData<ArrayList<AlarmSimpleData?>> = MutableLiveData()

    private val alarmDbRepository = AlarmDbRepository(
        AlarmsDB.getInstance(getApplication())?.alarmsDao()!!
    )

    suspend fun getAlarmsFromDbByDayOfWeek(dayOfWeek: Int?) = withContext(Dispatchers.IO) {
        if (dayOfWeek == null) alarmsList.postValue(ArrayList())
        else {
            alarmsList.postValue(
                alarmDbRepository.getAlarmsFromDbByDayOfWeek(
                    dayOfWeek,
                    calendarRepository.getDateOfCurrentWeekString(dayOfWeek)
                )
            )
        }
    }

    suspend fun getEarliestAlarmsForAllWeek() = withContext(Dispatchers.IO) {
        earliestAlarmsList.postValue(alarmDbRepository.getEarliestAlarmsFromDb(calendarRepository.getDatesForCurrentWeek()))
    }

    suspend fun setAlarmStateInDb(alarm: AlarmSimpleData) = withContext(Dispatchers.IO) {
        alarmDbRepository.updateAlarmInDb(alarm)
    }

    suspend fun deleteAlarmFromDb(alarm: AlarmSimpleData) = withContext(Dispatchers.IO) {
        alarmDbRepository.deleteAlarmFromDb(alarm)
    }

    fun timesToString(alarmsList: ArrayList<AlarmSimpleData?>): ArrayList<String> {
        return com.example.smartalarm.data.repositories.timesToString(alarmsList)
    }

    fun updateToday() {
        currentDayOfWeek = getTodayNumInWeek()
    }

    fun addInfoInformationToBundle(currentBundle: Bundle?, id: Long? = null): Bundle {
        val resultBundle = currentBundle ?: Bundle()
        with(resultBundle) {
            putIntegerArrayList(
                "currentDay", arrayListOf(
                    currentDayOfWeek!!,
                    weekCalendarData.weekOfYear,
                    weekCalendarData.daysList[currentDayOfWeek!!].yearNumber
                )
            )
            putStringArrayList("infoCurrentDay", getCurrentDateStringForAllWeek())
            putStringArrayList("infoCurrentDayOfWeek", getDateOfWeekStringForAllWeek())
            putStringArrayList("datesOfWeek", calendarRepository.getDatesForCurrentWeek())
            putBoolean("isNew", id == null)
            if (id != null) putLong("alarmId", id)
        }
        return resultBundle
    }

    fun changeWeek(next: Int) {
        currentDayOfWeek = null
        alarmsList.postValue(ArrayList())
        calendarRepository.changeWeek(next)
        weekCalendarData = calendarRepository.getWeek()
    }

    fun setDate(dayInfo: ArrayList<Int>) {
        currentDayOfWeek = dayInfo[0]
        weekCalendarData = calendarRepository.getWeekOfDay(dayInfo[1], dayInfo[2])
    }

    fun getInfoLine() =
        if (currentDayOfWeek == null)
            "Выберите день"
        else
            "Будильники на ${getCurrentDateOfWeekString(currentDayOfWeek!!)},\n${
                getCurrentDateString(
                    currentDayOfWeek!!
                )
            }:"

    fun getCurrentDateString(dayOfWeek: Int) =
        weekCalendarData.daysList[dayOfWeek].dayNumber.toString() + " " +
                getMontNameVinit(weekCalendarData.daysList[dayOfWeek].monthNumber)

    fun getCurrentDateOfWeekString(dayOfWeek: Int) =
        if (weekCalendarData.daysList[dayOfWeek].today)
            "сегодня"
        else if (calendarRepository.isAhead(dayOfWeek, 1))
            "завтра"
        else if (calendarRepository.isAhead(dayOfWeek, 2))
            "послезавтра"
        else
            getDayOfWeekNameVinit(dayOfWeek)

    fun getCurrentDateStringForAllWeek(): ArrayList<String> {
        val list = ArrayList<String>()
        for (i in 0..6)
            list.add(getCurrentDateString(i))
        return list
    }

    fun getDateOfWeekStringForAllWeek(): ArrayList<String> {
        val list = ArrayList<String>()
        for (i in 0..6)
            list.add(getCurrentDateOfWeekString(i))
        return list
    }
}