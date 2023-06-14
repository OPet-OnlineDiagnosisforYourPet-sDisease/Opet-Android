package com.example.meowbottom.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.meowbottom.api.ApiService
import com.example.meowbottom.response.StoriesItem
import com.example.meowbottom.ui.community.CommunityPagingSource

class CommunityRepository(private val apiService: ApiService) {

    fun getStory(token: String): LiveData<PagingData<StoriesItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                CommunityPagingSource(apiService, token)
            }
        ).liveData
    }
}