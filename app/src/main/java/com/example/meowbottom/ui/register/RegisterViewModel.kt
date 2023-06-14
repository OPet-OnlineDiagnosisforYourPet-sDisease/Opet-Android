package com.example.meowbottom.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel() {

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isError = MutableLiveData<Boolean?>()
    val isError: LiveData<Boolean?> = _isError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true

        val client = ApiConfig.getServiceCommunity().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && !response.body()!!.error!!) {
                    _message.value = response.body()!!.message
                    _isError.value = response.body()!!.error
                } else {
                    _isError.value = response.body()!!.error
                    _message.value = response.body()!!.message
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }
    companion object {
        private const val TAG = "RegisterViewModel"
    }
}