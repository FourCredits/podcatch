package four.credits.podcatch.domain

data class Episode(
    val title: String,
    val description: String,
    val link: String,
    val downloaded: Boolean = false,
    // TODO: do we want to keep the podcast id in the domain model?
    val podcastId: Long = 0,
    val id: Long = 0
)
