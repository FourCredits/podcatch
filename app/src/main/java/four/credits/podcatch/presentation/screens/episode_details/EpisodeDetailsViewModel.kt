package four.credits.podcatch.presentation.screens.episode_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.util.UnstableApi
import four.credits.podcatch.PodcatchApplication
import four.credits.podcatch.domain.DownloadManager
import four.credits.podcatch.domain.DownloadState
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.EpisodeRepository
import four.credits.podcatch.domain.PlayManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// TODO: build in abstractions
@OptIn(ExperimentalCoroutinesApi::class)
@androidx.annotation.OptIn(UnstableApi::class)
class EpisodeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: EpisodeRepository,
    private val downloadManager: DownloadManager,
    private val playManager: PlayManager,
) : ViewModel() {
    private val id = savedStateHandle.getStateFlow(IdArg, 0L)
    val episode = id
        .flatMapLatest(repository::getEpisodeById)
        .filterIsInstance<Episode>()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), loading)

    val downloadState = episode
        .flatMapLatest(downloadManager::downloadStatus)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            DownloadState.NotDownloaded
        )

    val isPlaying = playManager.currentlyPlaying()

    fun downloadEpisode() {
        viewModelScope.launch { downloadManager.download(episode.value) }
    }

    fun removeDownload() {
        viewModelScope.launch { downloadManager.deleteDownload(episode.value) }
    }

    fun playEpisode() {
        viewModelScope.launch { playManager.play(episode.value) }
    }

    fun pauseEpisode() {
        viewModelScope.launch { playManager.pause() }
    }

    companion object {
        val loading = Episode("loading...", "loading...", "loading...")

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PodcatchApplication
                EpisodeDetailsViewModel(
                    createSavedStateHandle(),
                    application.episodeRepository,
                    application.downloadManager,
                    application.playManager,
                )
            }
        }
    }
}
