package com.example.meowbottom.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.CommunityResponse
import com.example.meowbottom.response.KlinikItem
import com.example.meowbottom.response.MapClinicResponse
import com.example.meowbottom.response.StoriesItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listMap = MutableLiveData<List<StoriesItem?>?>()
    val listMap: LiveData<List<StoriesItem?>?> = _listMap

    private val _listClinic = MutableLiveData<List<KlinikItem?>?>()
    val listClinic: LiveData<List<KlinikItem?>?> = _listClinic

    fun listMap(token: String) {

        _isLoading.value =  true

        val client = ApiConfig.getServiceCommunity().getAllMaps(token)
        client.enqueue(object : Callback<CommunityResponse> {
            override fun onResponse(
                call: Call<CommunityResponse>,
                response: Response<CommunityResponse>
            ) {
                _isLoading.value = false
                //_isToast.value = true
                if (response.isSuccessful) {
                    _listMap.value = response.body()?.stories
                } else {
                    Log.d(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {
                _isLoading.value = true
                //_isToast.value = true
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }

    fun listClinic() {

        _isLoading.value =  true

        val client = ApiConfig.getServiceClinicMap().getAllMapsClinic()
        client.enqueue(object : Callback<MapClinicResponse> {
            override fun onResponse(
                call: Call<MapClinicResponse>,
                response: Response<MapClinicResponse>
            ) {
                _isLoading.value = false
                //_isToast.value = true
                if (response.isSuccessful && !response.body()!!.error!!) {
                    _listClinic.value = response.body()?.klinik
                } else {
                    Log.d(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MapClinicResponse>, t: Throwable) {
                _isLoading.value = true
                //_isToast.value = true
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "MapsViewModel"
    }
}