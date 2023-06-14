package com.example.meowbottom.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.meowbottom.database.History
import com.example.meowbottom.database.HistoryDao
import com.example.meowbottom.database.HistoryRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {

    private val mHistory: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryRoomDatabase.getDatabase(application)
        mHistory = db.historyDisease()
    }

    fun getAllHistories(username: String): LiveData<List<History>> = mHistory.getAllHistories(username)

    fun insert(history: History) {
        executorService.execute { mHistory.insert(history) }
    }

    /*fun delete(history: History) {
        executorService.execute { mHistory.delete(history) }
    }*/

    fun deleteByDiseaseAndUsername(disease: String, username: String) {
        executorService.execute { mHistory.deleteByDiseaseAndUsername(disease, username) }
    }

    fun getHistoryByDisease(disease: String, username: String): LiveData<History> = mHistory.getHistoryByDisease(disease, username)

}