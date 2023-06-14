package com.example.meowbottom.ui.detailCommunity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.response.*
import com.example.meowbottom.ui.upload.UploadViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailCommunityViewModel: ViewModel() {

    private val _story = MutableLiveData<Story?>()
    val story: LiveData<Story?> = _story

    private val _listComment = MutableLiveData<List<CommentsItem?>?>()
    val listComment: LiveData<List<CommentsItem?>?> = _listComment

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean?>()
    val isError: LiveData<Boolean?> = _isError

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    init {
        _listComment
    }

    fun storyDetail(id: Int) {
        _isLoading.value = true

        val client = ApiConfig.getServiceCommunity().getDetailStory(id)
        client.enqueue(object : Callback<DetailCommunityResponse> {
            override fun onResponse(call: Call<DetailCommunityResponse>, response: Response<DetailCommunityResponse>) {
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
                    _story.value = response.body()?.story
                    _listComment.value = response.body()?.story?.comments
                    _isError.value = response.body()?.error
                } else {
                    _isError.value = true
                    Log.e(TAG, "onResponse: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailCommunityResponse>, t: Throwable) {
                _isLoading.value = true
                _isError.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    fun postComment(token: String, id: Int, comment: RequestBody) {
        _isLoading.value = true

        val client = ApiConfig.getServiceCommunity().postComment(token, id, comment)
        client.enqueue(object : Callback<CommentResponse> {
            override fun onResponse(
                call: Call<CommentResponse>,
                response: Response<CommentResponse>
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

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }
    companion object {
        private const val TAG = "DetailCommunityViewModel"
    }
}