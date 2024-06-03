package four.credits.podcatch.presentation.screens.episode_details

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import four.credits.podcatch.PodcatchApplication
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.EpisodeRepository
import four.credits.podcatch.presentation.PodcastDownloadService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

// TODO: build in abstractions
// TODO: this shouldn't have to be an Android ViewModel. Make abstractions such
//  that it doesn't have to
@OptIn(ExperimentalCoroutinesApi::class)
@androidx.annotation.OptIn(UnstableApi::class)
class EpisodeDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val repository: EpisodeRepository,
    private val player: Player,
) : AndroidViewModel(application) {
    private val id = savedStateHandle.getStateFlow(IdArg, 0L)
    val episode = id
        .flatMapLatest(repository::getEpisodeById)
        .filterIsInstance<Episode>()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), loading)

    val downloadState = flow {
        val context = getApplication<PodcatchApplication>().applicationContext
        while (true) {
            delay(50.milliseconds)
            emit(determineState(getDownload(context)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), NotDownloaded)

    private fun determineState(download: Download?) = when (download?.state) {
        Download.STATE_COMPLETED -> Downloaded(isPlaying(download))
        Download.STATE_DOWNLOADING -> InProgress(download.percentDownloaded / 100)
        Download.STATE_RESTARTING, Download.STATE_QUEUED -> (InProgress(0f))
        Download.STATE_FAILED,
        Download.STATE_REMOVING,
        Download.STATE_STOPPED -> (NotDownloaded)
        else -> NotDownloaded
    }

    private fun isPlaying(download: Download) =
        download.request.id == player.currentMediaItem?.mediaId
        && player.isPlaying

    fun downloadEpisode(context: Context) {
        val link = episode.value.link
        val uri = Uri.parse(link)
        // using the link as the content id
        val downloadRequest = DownloadRequest.Builder(link, uri).build()
        DownloadService.sendAddDownload(
            context,
            PodcastDownloadService::class.java,
            downloadRequest,
            false
        )
    }

    fun removeDownload(context: Context) {
        val contentId = episode.value.link
        DownloadService.sendRemoveDownload(
            context,
            PodcastDownloadService::class.java,
            contentId,
            false
        )
    }

    fun playEpisode(context: Context) {
        if (player.currentMediaItem?.mediaId != episode.value.link) {
            viewModelScope.launch {
                val download = getDownload(context) ?: return@launch
                player.setMediaItem(download.request.toMediaItem())
            }
        }
        player.play()
    }

    private suspend fun getDownload(context: Context): Download? =
        viewModelScope.async {
            PodcastDownloadService.downloadManager
                .getOrCreate(context)
                .downloadIndex
                .getDownload(episode.value.link)
        }.await()

    fun pauseEpisode() = player.pause()

    companion object {
        val loading = Episode("loading...", "loading...", "loading...")

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PodcatchApplication
                EpisodeDetailsViewModel(
                    application,
                    createSavedStateHandle(),
                    application.episodeRepository,
                    application.player,
                )
            }
        }
    }
}

sealed interface DownloadState
data object NotDownloaded : DownloadState
data class Downloaded(val playing: Boolean) : DownloadState
data class InProgress(val progress: Float) : DownloadState
