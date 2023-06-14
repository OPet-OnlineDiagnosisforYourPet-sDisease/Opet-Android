package com.example.meowbottom.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.CameraDiseaseResponse
import com.example.meowbottom.response.UpdateProfileResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel:ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _photoUrl = MutableLiveData<String>()
    val photoUrl: LiveData<String> = _photoUrl

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError


    fun updatePhoto(token: String, input: MultipartBody.Part ) {

        _isLoading.value = true

        val client = ApiConfig.getServiceCommunity().updatePhotoProfile(token, input )
        client.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error!!) {
                        _isError.value = false
                        //_message.value = responseBody.message!!
                        _photoUrl.value = responseBody.profil!!
                    } else {
                        _isError.value = true
                        //_message.value = responseBody.message!!
                    }
                } else {
                    //_message.value = response.body()!!.message!!
                    _isError.value = true
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                _isError.value = true
                _isLoading.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "SymptomViewModel"
    }
}