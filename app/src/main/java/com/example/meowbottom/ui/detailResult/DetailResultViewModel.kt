package com.example.meowbottom.ui.detailResult

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.DiseaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailResultViewModel: ViewModel() {

    private val _name = MutableLiveData<String?>()
    val name: LiveData<String?> = _name

    private val _deskripsi = MutableLiveData<List<String?>?>()
    val deskripsi: LiveData<List<String?>?> = _deskripsi

    private val _penanganan = MutableLiveData<List<String?>?>()
    val penanganan: LiveData<List<String?>?> = _penanganan

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDisease(name : String) {

        _isLoading.value = true
        
        val client = ApiConfig.getServiceDisease().getDisease(name)
        client.enqueue(object : Callback<DiseaseResponse> {
            override fun onResponse(
                call: Call<DiseaseResponse>,
                response: Response<DiseaseResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _name.value = response.body()!!.nama
                    _deskripsi.value = response.body()!!.deskripsiPenyakit
                    _penanganan.value = response.body()!!.penanganan
                } else {
                    Log.d(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DiseaseResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })


    }
    companion object {
        private const val TAG = "DetailResultViewModel"
    }
}