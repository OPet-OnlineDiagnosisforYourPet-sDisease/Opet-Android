package com.example.meowbottom.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.ArticleItem
import com.example.meowbottom.response.ArticleResponse
import com.example.meowbottom.response.StoriesResponse
import com.example.meowbottom.ui.symptom.SymptomViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel: ViewModel() {

    private val _listArticle = MutableLiveData<List<ArticleItem?>?>()
    val listArticle: LiveData<List<ArticleItem?>?> = _listArticle

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun listArticle() {

        _isLoading.value =  true

        val client = ApiConfig.getServiceArticle().getAllArticle()
        client.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listArticle.value = response.body()?.artikel
                } else {
                    Log.d(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                _isLoading.value = true
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}