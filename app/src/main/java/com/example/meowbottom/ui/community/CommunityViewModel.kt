package com.example.meowbottom.ui.community

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.CommunityResponse
import com.example.meowbottom.response.StoriesItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityViewModel: ViewModel() {

    private val _listStory = MutableLiveData<List<StoriesItem?>?>()
    val listStory: LiveData<List<StoriesItem?>?> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun listStory(token: String) {

        _isLoading.value =  true

        val client = ApiConfig.getServiceCommunity().getAllCommunity(token)
        client.enqueue(object : Callback<CommunityResponse> {
            override fun onResponse(
                call: Call<CommunityResponse>,
                response: Response<CommunityResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.stories
                } else {
                    Log.d(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {
                _isLoading.value = true
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "CommunityViewModel"
    }

}