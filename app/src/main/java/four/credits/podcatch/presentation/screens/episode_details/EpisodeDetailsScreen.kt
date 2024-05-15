package four.credits.podcatch.presentation.screens.episode_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
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
import four.credits.podcatch.domain.DownloadProgress
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.presentation.theme.AppIcons
import four.credits.podcatch.presentation.theme.PodcatchTheme

fun NavGraphBuilder.episodeDetailsScreen() = composable(
    "$EpisodeDetailsRoute/{$IdArg}",
    arguments = listOf(navArgument(IdArg) { type = NavType.LongType })
) {
    val viewModel = viewModel<EpisodeDetailsViewModel>(
        factory = EpisodeDetailsViewModel.Factory,
    )
    val episode by viewModel.episode.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()
    EpisodeDetailsScreen(
        episode,
        downloadState,
        viewModel::downloadEpisode,
        viewModel::deleteEpisode,
        viewModel::playEpisode,
        viewModel::pauseEpisode,
    )
}

fun NavController.navigateToEpisode(id: Long) =
    navigate("$EpisodeDetailsRoute/$id")

private const val EpisodeDetailsRoute = "episode_details"
internal const val IdArg = "episodeId"

// TODO: display podcast name
@Composable
private fun EpisodeDetailsScreen(
    episode: Episode,
    downloadState: DownloadState,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
) {
    Column {
        // TODO: don't hardcode font sizes
        Text(text = episode.title, fontSize = 24.sp)
        HorizontalDivider()
        Text(text = episode.description, modifier = Modifier.weight(1f))
        HorizontalDivider()
        BottomPanel(
            downloadState,
            onDelete,
            onDownload,
            onPlay,
            onPause
        )
    }
}

@Composable
private fun BottomPanel(
    downloadState: DownloadState,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically,
) {
    when (downloadState) {
        is Downloaded -> {
            Icon(
                painterResource(R.drawable.download_done),
                stringResource(R.string.download_completed)
            )
            val isPlaying = downloadState.playState is Playing
            IconButton(onClick = { if (isPlaying) onPause() else onPlay() }) {
                if (isPlaying) {
                    Icon(
                        painterResource(id = R.drawable.pause),
                        "Pause podcast",
                    )
                } else {
                    Icon(
                        AppIcons.PlayArrow,
                        "Play podcast",
                    )

                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    AppIcons.Delete,
                    stringResource(R.string.delete_episode)
                )
            }
        }

        is InProgress -> {
            ProgressIndication(downloadState)
            // TODO: option to cancel a download partway through
        }

        NotDownloaded -> IconButton(onClick = onDownload) {
            Icon(
                painterResource(R.drawable.download),
                stringResource(R.string.download_episode)
            )
        }
    }
}

@Composable
private fun ProgressIndication(downloadState: InProgress) {
    val amount = downloadState.downloadProgress.amountDownloaded()
    Row {
        CircularProgressIndicator(progress = { amount })
        Text("${(amount * 100).toUInt()}%")
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomPanelPreview() = PodcatchTheme {
    Column {
        BottomPanel(
            downloadState = Downloaded(Playing),
            onDelete = {},
            onDownload = {},
            onPlay = {},
            onPause = {},
        )
        BottomPanel(
            downloadState = Downloaded(Paused),
            onDelete = {},
            onDownload = {},
            onPlay = {},
            onPause = {},
        )
        BottomPanel(
            downloadState = Downloaded(NotStarted),
            onDelete = {},
            onDownload = {},
            onPlay = {},
            onPause = {},
        )
        BottomPanel(
            downloadState = NotDownloaded,
            onDelete = {},
            onDownload = {},
            onPlay = {},
            onPause = {},
        )
        BottomPanel(
            downloadState = InProgress(DownloadProgress(2345, 7652)),
            onDelete = {},
            onDownload = {},
            onPlay = {},
            onPause = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EpisodeDetailsScreenPreview() {
    PodcatchTheme {
        EpisodeDetailsScreen(
            episode = Episode(
                "My example episode",
                "A description for the episode",
                "shouldn't be shown",
            ),
            downloadState = InProgress(DownloadProgress(25, 100)),
            onDownload = {},
            onDelete = {},
            onPlay = {},
            onPause = {},
        )
    }
}
