package four.credits.podcatch.presentation.screens.podcast_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import four.credits.podcatch.R
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.presentation.theme.AppIcons
import four.credits.podcatch.presentation.theme.LocalSpacing
import four.credits.podcatch.presentation.theme.PodcatchTheme

@Composable
fun PodcastDetailsScreen(
    viewModel: PodcastDetailsViewModel,
    onNavigateUp: () -> Unit,
) {
    val podcast by viewModel.podcast.collectAsStateWithLifecycle()
    PodcastDetailsScreenInternal(podcast, onDelete = {
        viewModel.delete()
        onNavigateUp()
    })
}

@Composable
private fun PodcastDetailsScreenInternal(
    podcast: Podcast,
    onDelete: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
    ) {
        // TODO: don't hardcode font sizes
        item {
            Text(podcast.title, fontSize = 24.sp)
            HorizontalDivider()
            Text(podcast.description)
        }
        items(items = podcast.episodes, key = { it.id }) {
            EpisodeDisplay(episode = it)
        }
        item {
            IconButton(onClick = onDelete) {
                Icon(AppIcons.Delete, stringResource(R.string.alt_delete_podcast))
            }
        }
    }
}

@Composable
private fun EpisodeDisplay(episode: Episode) {
    Card {
        Text(episode.title, fontSize = 16.sp)
        HorizontalDivider()
        Text(episode.description)
    }
}

@Preview
@Composable
private fun PodcastDetailsScreenPreview() {
    PodcatchTheme {
        PodcastDetailsScreenInternal(
            Podcast(
                title = "My very important podcast",
                description = "A podcast about android development",
                link = "This shouldn't be visible",
                // TODO: implement displaying podcasts
                listOf(),
            ),
            onDelete = {},
        )
    }
}
