package com.example.smartalarm.data.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.smartalarm.data.data.AccountData
import com.example.smartalarm.data.db.GameData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UsersRealtimeDatabaseRepository {
    private val usersDatabase = FirebaseDatabase
        .getInstance("https://smartalarm-ccdbb-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("users")

    private val topRecordsDatabase = FirebaseDatabase
        .getInstance("https://smartalarm-ccdbb-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("top_records")

    suspend fun addUser(account: AccountData?) = withContext(Dispatchers.IO) {
        if (account != null) {
            usersDatabase.child(account.uid!!).get().addOnSuccessListener {
                Log.i("grib", "GotValue")
                if (!it.exists())
                    usersDatabase.child(account.uid!!).setValue(account)
            }
        }
    }

    suspend fun getTopRecords(userList: MutableLiveData<List<AccountData>>) =
        withContext(Dispatchers.IO) {
            topRecordsDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.postValue(
                        snapshot.children.map { dataSnapshot ->
                            dataSnapshot.getValue(AccountData::class.java)!!
                        }
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", error.toString())
                }

            })
        }

    suspend fun getAllUsers(userList: MutableLiveData<List<AccountData>>) =
        withContext(Dispatchers.IO) {
            usersDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.postValue(
                        snapshot.children.map { dataSnapshot ->
                            dataSnapshot.getValue(AccountData::class.java)!!
                        }
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", error.toString())
                }

            })
        }

    suspend fun updateUserRecords(account: AccountData, gameData: GameData) =
        withContext(Dispatchers.IO) {
            val recordUserDb = usersDatabase.child(account.uid!!)
            val topRecordDb = topRecordsDatabase.child(gameData.id.toString())
            recordUserDb.get().addOnSuccessListener {
                val current = it.getValue(AccountData::class.java)

                if (current?.records == "null")
                    current.records = gameData.toString()
                else
                    current?.records += "/$gameData"

                recordUserDb.updateChildren(
                    mapOf(
                        "email" to current?.email,
                        "name" to current?.name,
                        "photo" to current?.photo,
                        "records" to current?.records,
                        "uid" to current?.uid
                    )
                ).addOnSuccessListener {
                    topRecordDb.get().addOnSuccessListener {
                        current?.records = gameData.toString()
                        if (it.exists()) {
                            if (GameData(it.getValue(AccountData::class.java)?.records!!).record!! < gameData.record!!)
                                topRecordDb.updateChildren(
                                    mapOf(
                                        "email" to current?.email,
                                        "name" to current?.name,
                                        "photo" to current?.photo,
                                        "records" to current?.records,
                                        "uid" to current?.uid
                                    )
                                )
                        } else {
                            topRecordDb.setValue(current)
                        }
                    }

                }
            }
        }
}