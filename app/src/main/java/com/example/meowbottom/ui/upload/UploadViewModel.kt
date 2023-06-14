package com.example.meowbottom.ui.upload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.DataItem
import com.example.meowbottom.response.PostCommunityResponse
import com.example.meowbottom.response.PostStoryResponse
import com.example.meowbottom.ui.symptom.SymptomViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isError = MutableLiveData<Boolean?>()
    val isError : LiveData<Boolean?> = _isError

    fun postStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null) {
        _isLoading.value = true

        val client = ApiConfig.getServiceCommunity().postCommunity(token, file, description, lat, lon)
        client.enqueue(object : Callback<PostCommunityResponse> {
            override fun onResponse(
                call: Call<PostCommunityResponse>,
                response: Response<PostCommunityResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error!!) {
                        _message.value = responseBody.message!!
                        _isError.value = responseBody.error
                    } else {
                        _message.value = responseBody!!.message!!
                        _isError.value = responseBody.error
                    }
                } else {
                    _message.value = response.message()
                    _isError.value = response.body()!!.error!!
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostCommunityResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "UploadViewModel"
    }
}