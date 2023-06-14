package com.example.meowbottom.ui.community

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.meowbottom.api.ApiConfig
import com.example.meowbottom.repository.CommunityRepository
import com.example.meowbottom.response.StoriesItem

class CommunityPageViewModel(private val storyRepository: CommunityRepository): ViewModel() {

    fun story(token: String): LiveData<PagingData<StoriesItem>> = storyRepository.getStory(token).cachedIn(viewModelScope)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityPageViewModel(Injection.provideRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

object Injection {
    fun provideRepository(): CommunityRepository {
        val apiService = ApiConfig.getServiceCommunity()
        return CommunityRepository(apiService)
    }
}