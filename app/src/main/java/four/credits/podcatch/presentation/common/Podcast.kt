package four.credits.podcatch.presentation.common

import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import four.credits.podcatch.domain.Podcast

@Composable
fun PodcastDisplay(podcast: Podcast) {
    Card {
        Text(text = podcast.title)
        HorizontalDivider()
        Text(text = podcast.description)
    }
}
