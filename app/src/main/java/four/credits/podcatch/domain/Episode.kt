package four.credits.podcatch.domain

data class Episode(
    val title: String,
    val description: String,
    val link: String,
    val downloaded: Boolean = false,
    val id: Long = 0
)
