package com.example.meowbottom.ui.symptom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SymptomViewModel: ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem?>?>()
    val listStory: LiveData<List<ListStoryItem?>?> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listSymptom = MutableLiveData<List<DataItem?>?>()
    val listSymptom: LiveData<List<DataItem?>?> = _listSymptom

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _symptom = MutableLiveData<List<String>>()
    val symptom: MutableLiveData<List<String>> = _symptom

    init {
        //listStory
        listSymptom()
    }

    fun listStory(token: String) {

        _isLoading.value =  true

        val client = ApiConfig.getServiceStory().getAllStories(token)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory
                } else {
                    Log.d(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _isLoading.value = true
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }
    fun listSymptom() {

        _isLoading.value =  true

        val client = ApiConfig.getService().getAllSymptom()
        client.enqueue(object : Callback<SymptomResponse> {
            override fun onResponse(
                call: Call<SymptomResponse>,
                response: Response<SymptomResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listSymptom.value = response.body()?.data
                } else {
                    Log.d(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SymptomResponse>, t: Throwable) {
                _isLoading.value = true
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }
    fun postSymptom(gejala: List<String>) {
        _isLoading.value = true

        val client = ApiConfig.getService().postSymptom(gejala)
        client.enqueue(object : Callback<PostSymptomResponse> {
            override fun onResponse(
                call: Call<PostSymptomResponse>,
                response: Response<PostSymptomResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success!!) {
                        _message.value = responseBody.message!!
                        _symptom.value = responseBody.gejala as List<String>?
                    }
                } else {
                    _message.value = response.message()
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostSymptomResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
    fun checkSymptom(gejala: String) {
        _isLoading.value = true

        val client = ApiConfig.getServiceML().checkSymptom(gejala)
        client.enqueue(object : Callback<CheckSymptomResponse> {
            override fun onResponse(
                call: Call<CheckSymptomResponse>,
                response: Response<CheckSymptomResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _message.value = responseBody.prediction!!
                    }
                } else {
                    _message.value = response.message()
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CheckSymptomResponse>, t: Throwable) {
                _message.value = t.message
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun postStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null) {
        _isLoading.value = true

        val client = ApiConfig.getServiceStory().postStories(token, file, description, lat, lon)
        client.enqueue(object : Callback<PostStoryResponse> {
            override fun onResponse(
                call: Call<PostStoryResponse>,
                response: Response<PostStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error!!) {
                        _message.value = responseBody.message!!
                    }
                } else {
                    _message.value = response.message()
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostStoryResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "SymptomViewModel"
    }
}