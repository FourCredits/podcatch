package four.credits.podcatch.domain

data class Podcast(
    val title: String,
    val description: String,
    val link: String,
    val episodes: List<Episode>,
    val id: Long = 0,
)
