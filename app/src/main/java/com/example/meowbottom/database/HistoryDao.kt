package com.example.meowbottom.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(history: History)

    /*@Delete
    fun delete(history: History)*/
    @Query("DELETE FROM history WHERE disease = :disease AND username = :username")
    fun deleteByDiseaseAndUsername(disease: String, username: String)

    @Query("SELECT * FROM history WHERE username = :username ORDER BY id DESC")
    fun getAllHistories(username: String): LiveData<List<History>>

    @Query("SELECT * FROM history WHERE disease = :disease AND username = :username")
    fun getHistoryByDisease(disease: String, username: String): LiveData<History>
}