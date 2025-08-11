package tellme.sairajpatil108.tellme360.data.model

data class VideoContent(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val duration: String,
    val category: String,
    val isVR: Boolean = false,
    val rating: Float = 0f,
    val views: Int = 0
)

data class Series(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val episodeCount: Int,
    val category: String,
    val rating: Float = 0f
)

data class ContentCategory(
    val id: String,
    val name: String,
    val icon: String
)

