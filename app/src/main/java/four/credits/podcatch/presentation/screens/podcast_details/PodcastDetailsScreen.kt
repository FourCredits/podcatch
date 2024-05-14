package four.credits.podcatch.presentation.screens.podcast_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
        onEpisodeClick = onEpisodeClick,
        onDownload = { _ -> },
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
    onDownload: (Episode) -> Unit,
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
            EpisodeDisplay(
                episode = it,
                onClick = { onEpisodeClick(it.id) },
                onDownload = { onDownload(it) }
            )
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
private fun EpisodeDisplay(
    episode: Episode,
    onClick: () -> Unit,
    onDownload: () -> Unit,
) {
    Card(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                episode.title,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            if (episode.downloaded) {
                Icon(
                    painterResource(id = R.drawable.download_done),
                    stringResource(id = R.string.download_completed)
                )
            } else {
                IconButton(onClick = onDownload) {
                    Icon(
                        painterResource(id = R.drawable.download),
                        stringResource(id = R.string.download_episode)
                    )
                }
            }
        }
        HorizontalDivider()
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
                        downloaded = true,
                    )
                ),
            ),
            onDelete = {},
            onEpisodeClick = {},
            onDownload = { _ -> },
        )
    }
}
