package four.credits.podcatch.presentation.screens.episode_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import four.credits.podcatch.domain.DownloadProgress
import four.credits.podcatch.domain.DownloadState
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
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    LocalContext.current
    EpisodeDetailsScreen(
        episode = episode,
        downloadState = downloadState,
        isPlaying = isPlaying,
        onDownload = viewModel::downloadEpisode,
        onDelete = viewModel::removeDownload,
        onCancel = viewModel::cancelDownload,
        onPlay = viewModel::playEpisode,
        onPause = viewModel::pauseEpisode,
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
    isPlaying: Boolean,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
) {
    Column {
        Text(
            text = episode.title,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(text = episode.description, modifier = Modifier.weight(1f))
        BottomPanel(
            downloadState,
            isPlaying,
            onDownload,
            onDelete,
            onCancel,
            onPlay,
            onPause
        )
    }
}

@Composable
private fun BottomPanel(
    downloadState: DownloadState,
    isPlaying: Boolean,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically,
) {
    when (downloadState) {
        DownloadState.Downloaded -> {
            Icon(
                painterResource(R.drawable.download_done),
                stringResource(R.string.download_completed)
            )
            IconButton(onClick = { if (isPlaying) onPause() else onPlay() }) {
                if (isPlaying) {
                    Icon(
                        painterResource(id = R.drawable.pause),
                        stringResource(R.string.pause_episode),
                    )
                } else {
                    Icon(
                        AppIcons.PlayArrow,
                        stringResource(R.string.play_episode),
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

        is DownloadState.InProgress -> {
            ProgressIndication(downloadState.progress)
            IconButton(onClick = onCancel) {
                Icon(AppIcons.Close, stringResource(R.string.cancel_download))
            }
        }

        DownloadState.NotDownloaded -> IconButton(onClick = onDownload) {
            Icon(
                painterResource(R.drawable.download),
                stringResource(R.string.download_episode)
            )
        }
    }
}

@Composable
private fun ProgressIndication(downloadProgress: DownloadProgress) = Row {
    CircularProgressIndicator(progress = { downloadProgress.asDecimal() })
    Text("${downloadProgress.percentage.toUInt()}%")
}

@Preview(showBackground = true)
@Composable
private fun BottomPanelPreview() = PodcatchTheme {
    Column {
        BottomPanel(DownloadState.Downloaded, true, {}, {}, {}, {}, {})
        BottomPanel(DownloadState.Downloaded, false, {}, {}, {}, {}, {})
        BottomPanel(DownloadState.NotDownloaded, false, {}, {}, {}, {}, {})
        val progress = DownloadProgress(2345f / 7652f)
        BottomPanel(
            DownloadState.InProgress(progress),
            false,
            {},
            {},
            {},
            {},
            {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EpisodeDetailsScreenPreview() = PodcatchTheme {
    val inProgress = DownloadState.InProgress(DownloadProgress(25f / 100f))
    val episode = Episode(
        "My example episode",
        "A description for the episode",
        "shouldn't be shown",
    )
    EpisodeDetailsScreen(episode, inProgress, false, {}, {}, {}, {}, {})
}
