package com.example.meowbottom.ui.history

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.database.History
import com.example.meowbottom.repository.HistoryRepository

class HistoryViewModel(application: Application): ViewModel() {

    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)

    fun getAllHistories(username: String): LiveData<List<History>> = mHistoryRepository.getAllHistories(username)

    fun getHistoryByDisease(disease: String, username: String): LiveData<History> = mHistoryRepository.getHistoryByDisease(disease, username)

    fun insert(history: History) {
        mHistoryRepository.insert(history)
    }

    /*fun delete(history: History) {
        mHistoryRepository.delete(history)
    }*/
    fun deleteByDiseaseAndUsername(disease: String, username: String) {
        mHistoryRepository.deleteByDiseaseAndUsername(disease, username)
    }
}