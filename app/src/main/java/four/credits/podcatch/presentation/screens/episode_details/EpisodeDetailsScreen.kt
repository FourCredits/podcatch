package four.credits.podcatch.presentation.screens.episode_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.Delete
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
) {
    Column {
        // TODO: don't hardcode font sizes
        Text(text = episode.title, fontSize = 24.sp)
        HorizontalDivider()
        Text(text = episode.description, modifier = Modifier.weight(1f))
        HorizontalDivider()
        BottomPanel(downloadState, onDelete, onDownload)
    }
}

@Composable
private fun BottomPanel(
    downloadState: DownloadState,
    onDelete: () -> Unit,
    onDownload: () -> Unit
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically,
) {
    when (downloadState) {
        Downloaded -> {
            Icon(
                painterResource(R.drawable.download_done),
                stringResource(R.string.download_completed)
            )
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
    val downloaded = downloadState.downloadedBytes.toFloat()
    val total = downloadState.totalBytes.toFloat()
    Row {
        CircularProgressIndicator(progress = { downloaded / total })
        Text("${((downloaded / total) * 100).toUInt()}%")
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomPanelPreview() = PodcatchTheme {
    Column {
        BottomPanel(
            downloadState = Downloaded,
            onDelete = {},
            onDownload = {}
        )
        BottomPanel(
            downloadState = NotDownloaded,
            onDelete = {},
            onDownload = {}
        )
        BottomPanel(
            downloadState = InProgress(2345, 7652),
            onDelete = {},
            onDownload = {}
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
            downloadState = InProgress(25, 100),
            onDownload = {},
            onDelete = {},
        )
    }
}
