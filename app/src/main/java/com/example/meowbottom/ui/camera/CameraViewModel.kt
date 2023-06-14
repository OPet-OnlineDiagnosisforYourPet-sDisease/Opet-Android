package com.example.meowbottom.ui.camera

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.CameraDiseaseResponse
import com.example.meowbottom.response.DataItem
import com.example.meowbottom.response.ListStoryItem
import com.example.meowbottom.response.PostStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CameraViewModel: ViewModel() {


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError


    fun checkDisease(input: MultipartBody.Part) {

        _isLoading.value = true

        val client = ApiConfig.getServiceCameraDisease().checkDiseaseCamera(input)
        client.enqueue(object : Callback<CameraDiseaseResponse> {
            override fun onResponse(
                call: Call<CameraDiseaseResponse>,
                response: Response<CameraDiseaseResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _isError.value = false
                        _message.value = responseBody.prediction!!
                    }
                } else {
                    _message.value = response.message()
                    _isError.value = true
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CameraDiseaseResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "SymptomViewModel"
    }
}