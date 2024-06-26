package four.credits.podcatch.presentation.screens.podcast_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import four.credits.podcatch.R
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.presentation.theme.AppIcons
import four.credits.podcatch.presentation.theme.LocalSpacing
import four.credits.podcatch.presentation.theme.PodcatchTheme

fun NavGraphBuilder.podcastDetailsScreen(
    onNavigateUp: () -> Unit,
    onEpisodeClick: (Long) -> Unit,
) = composable(
    "$PodcastDetailsRoute/{$IdArg}",
    arguments = listOf(navArgument(IdArg) { type = NavType.LongType })
) {
    val viewModel = viewModel<PodcastDetailsViewModel>(
        factory = PodcastDetailsViewModel.Factory,
    )
    val podcast by viewModel.podcast.collectAsStateWithLifecycle()
    PodcastDetailsScreen(
        podcast,
        onDelete = {
            viewModel.delete()
            onNavigateUp()
        },
        onEpisodeClick = onEpisodeClick
    )
}

fun NavController.navigateToPodcast(id: Long) =
    navigate("$PodcastDetailsRoute/$id")

private const val PodcastDetailsRoute = "podcast_details"
internal const val IdArg = "podcastId"

@Composable
private fun PodcastDetailsScreen(
    podcast: Podcast,
    onDelete: () -> Unit,
    onEpisodeClick: (Long) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
    ) {
        item {
            Text(podcast.title, style = MaterialTheme.typography.headlineLarge)
            Text(
                podcast.description,
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(items = podcast.episodes, key = { it.id }) {
            EpisodeDisplay(episode = it, onClick = { onEpisodeClick(it.id) })
        }
        item {
            IconButton(onClick = onDelete) {
                Icon(
                    AppIcons.Delete,
                    stringResource(R.string.alt_delete_podcast)
                )
            }
        }
    }
}

@Composable
private fun EpisodeDisplay(episode: Episode, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Text(episode.title, style = MaterialTheme.typography.titleMedium)
        Text(episode.description)
    }
}

@Preview(showBackground = true)
@Composable
private fun PodcastDetailsScreenPreview() {
    PodcatchTheme {
        PodcastDetailsScreen(
            Podcast(
                title = "My very important podcast",
                description = "A podcast about android development",
                link = "This shouldn't be visible",
                episodes = listOf(
                    Episode(
                        title = "Episode 1",
                        description = "A cool description",
                        link = "",
                        id = 1,
                    ),
                    Episode(
                        title = "Episode 2",
                        description = "A cool description",
                        link = "",
                        id = 2,
                    )
                ),
            ),
            onDelete = {},
            onEpisodeClick = {},
        )
    }
}
