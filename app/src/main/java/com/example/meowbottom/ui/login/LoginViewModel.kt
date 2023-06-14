package com.example.meowbottom.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.LoginResponse
import com.example.meowbottom.response.LoginResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {

    private val _login = MutableLiveData<LoginResult?>()
    val login: LiveData<LoginResult?> = _login

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isError = MutableLiveData<Boolean?>()
    val isError: LiveData<Boolean?> = _isError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun login(email: String, password: String) {

        _isLoading.value = true

        val client = ApiConfig.getServiceCommunity().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && !response.body()!!.error!!) {
                    /*if (response.body()?.error!!) {
                        _isError.value = response.body()?.error
                        _message.value = response.body()?.message
                    } else {
                        _login.value = response.body()?.loginResult
                        _message.value = response.body()?.message
                        _isError.value = response.body()?.error
                    }*/
                    _login.value = response.body()?.loginResult
                    _message.value = response.body()?.message
                } else {
                    Log.e(TAG, "onResponse: ${response.message()}")
                    _message.value = response.body()?.message
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }
    companion object {
        private const val TAG = "LoginViewModel"
    }
}