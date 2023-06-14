package com.example.meowbottom.ui.community

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.meowbottom.api.ApiService
import com.example.meowbottom.response.StoriesItem

class CommunityPagingSource(private val apiService: ApiService, token: String) : PagingSource<Int, StoriesItem>() {

    val token = token
    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, StoriesItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoriesItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = token
            val responseData = apiService.getPageStories(token, position, params.loadSize)
            val stories = responseData.stories

            LoadResult.Page(
                data = stories,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (stories.isNullOrEmpty()) null else position + 1
            )
        } catch (exception : Exception) {
            return LoadResult.Error(exception)
        }
    }

}