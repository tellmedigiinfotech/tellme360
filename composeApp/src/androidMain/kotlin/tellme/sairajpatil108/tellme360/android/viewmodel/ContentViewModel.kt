package tellme.sairajpatil108.tellme360.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import tellme.sairajpatil108.tellme360.android.repository.FirestoreRepository
import tellme.sairajpatil108.tellme360.data.model.VideoContent
import tellme.sairajpatil108.tellme360.data.model.Series
import tellme.sairajpatil108.tellme360.data.model.ContentCategory

class ContentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FirestoreRepository()
    
    // Loading states
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Video data
    private val _allVideos = MutableLiveData<List<VideoContent>>(emptyList())
    val allVideos: LiveData<List<VideoContent>> = _allVideos
    
    private val _vrVideos = MutableLiveData<List<VideoContent>>(emptyList())
    val vrVideos: LiveData<List<VideoContent>> = _vrVideos
    
    private val _videosByCategory = MutableLiveData<List<VideoContent>>(emptyList())
    val videosByCategory: LiveData<List<VideoContent>> = _videosByCategory
    
    private val _selectedVideo = MutableLiveData<VideoContent?>()
    val selectedVideo: LiveData<VideoContent?> = _selectedVideo
    
    // Series data
    private val _allSeries = MutableLiveData<List<Series>>(emptyList())
    val allSeries: LiveData<List<Series>> = _allSeries
    
    private val _seriesByCategory = MutableLiveData<List<Series>>(emptyList())
    val seriesByCategory: LiveData<List<Series>> = _seriesByCategory
    
    private val _selectedSeries = MutableLiveData<Series?>()
    val selectedSeries: LiveData<Series?> = _selectedSeries
    
    // Category data
    private val _categories = MutableLiveData<List<ContentCategory>>(emptyList())
    val categories: LiveData<List<ContentCategory>> = _categories
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load all data in parallel from Firestore
                val categoriesDeferred = async { repository.getAllCategories() }
                val videosDeferred = async { repository.getAllVideos() }
                val vrVideosDeferred = async { repository.getVRVideos() }
                val seriesDeferred = async { repository.getAllSeries() }
                
                _categories.value = categoriesDeferred.await()
                _allVideos.value = videosDeferred.await()
                _vrVideos.value = vrVideosDeferred.await()
                _allSeries.value = seriesDeferred.await()
                
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load content: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadVideosByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val videos = repository.getVideosByCategory(category)
                _videosByCategory.value = videos
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load videos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadSeriesByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val series = repository.getSeriesByCategory(category)
                _seriesByCategory.value = series
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load series: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadVideoById(videoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val video = repository.getVideoById(videoId)
                _selectedVideo.value = video
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load video: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadSeriesById(seriesId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val series = repository.getSeriesById(seriesId)
                _selectedSeries.value = series
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load series: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshAllData() {
        loadInitialData()
    }
    
    fun clearError() {
        _error.value = null
    }
    
    // Helper function to get featured content (highest rated)
    fun getFeaturedVideos(): List<VideoContent> {
        return _allVideos.value?.sortedByDescending { it.rating }?.take(5) ?: emptyList()
    }
    
    fun getFeaturedSeries(): List<Series> {
        return _allSeries.value?.sortedByDescending { it.rating }?.take(5) ?: emptyList()
    }
    
    fun getPopularVideos(): List<VideoContent> {
        return _allVideos.value?.sortedByDescending { it.views }?.take(5) ?: emptyList()
    }
    

}
