package tellme.sairajpatil108.tellme360.android.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import tellme.sairajpatil108.tellme360.data.model.VideoContent
import tellme.sairajpatil108.tellme360.data.model.Series
import tellme.sairajpatil108.tellme360.data.model.ContentCategory

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val VIDEOS_COLLECTION = "videos"
        private const val SERIES_COLLECTION = "series"
        private const val CATEGORIES_COLLECTION = "categories"
    }
    
    // Video operations
    suspend fun getAllVideos(): List<VideoContent> {
        return try {
            val snapshot = firestore.collection(VIDEOS_COLLECTION)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(VideoContent::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getVRVideos(): List<VideoContent> {
        return try {
            val snapshot = firestore.collection(VIDEOS_COLLECTION)
                .whereEqualTo("isVR", true)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(VideoContent::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getVideosByCategory(category: String): List<VideoContent> {
        return try {
            val snapshot = firestore.collection(VIDEOS_COLLECTION)
                .whereEqualTo("category", category)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(VideoContent::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getVideoById(videoId: String): VideoContent? {
        return try {
            val snapshot = firestore.collection(VIDEOS_COLLECTION)
                .document(videoId)
                .get()
                .await()
            
            snapshot.toObject(VideoContent::class.java)?.copy(id = snapshot.id)
        } catch (e: Exception) {
            null
        }
    }
    
    // Series operations
    suspend fun getAllSeries(): List<Series> {
        return try {
            val snapshot = firestore.collection(SERIES_COLLECTION)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Series::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getSeriesById(seriesId: String): Series? {
        return try {
            val snapshot = firestore.collection(SERIES_COLLECTION)
                .document(seriesId)
                .get()
                .await()
            
            snapshot.toObject(Series::class.java)?.copy(id = snapshot.id)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getSeriesByCategory(category: String): List<Series> {
        return try {
            val snapshot = firestore.collection(SERIES_COLLECTION)
                .whereEqualTo("category", category)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Series::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Category operations
    suspend fun getAllCategories(): List<ContentCategory> {
        return try {
            val snapshot = firestore.collection(CATEGORIES_COLLECTION)
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ContentCategory::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Data insertion methods (for sample data initialization)
    suspend fun addVideo(video: VideoContent): Boolean {
        return try {
            firestore.collection(VIDEOS_COLLECTION)
                .add(video)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun addSeries(series: Series): Boolean {
        return try {
            firestore.collection(SERIES_COLLECTION)
                .add(series)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun addCategory(category: ContentCategory): Boolean {
        return try {
            firestore.collection(CATEGORIES_COLLECTION)
                .add(category)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Check if collections exist and have data
    suspend fun isDataInitialized(): Boolean {
        return try {
            val videosSnapshot = firestore.collection(VIDEOS_COLLECTION).limit(1).get().await()
            val seriesSnapshot = firestore.collection(SERIES_COLLECTION).limit(1).get().await()
            val categoriesSnapshot = firestore.collection(CATEGORIES_COLLECTION).limit(1).get().await()
            
            !videosSnapshot.isEmpty && !seriesSnapshot.isEmpty && !categoriesSnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}
