package four.credits.podcatch.presentation.screens.episode_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import four.credits.podcatch.PodcatchApplication
import four.credits.podcatch.domain.DownloadProgress
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.EpisodeRepository
import four.credits.podcatch.domain.PlayManager
import four.credits.podcatch.domain.PlayState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: EpisodeRepository,
    private val playManager: PlayManager,
) : ViewModel() {
    private val id = savedStateHandle.getStateFlow(IdArg, 0L)
    val episode = id
        .flatMapLatest(repository::getEpisodeById)
        .filterIsInstance<Episode>()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), loading)

    private val _downloadProgress = MutableStateFlow<DownloadProgress?>(null)

    val downloadState = combine(
        _downloadProgress,
        playManager.currentlyPlaying(),
        episode,
        ::determineDownloadState
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), NotDownloaded)

    private fun determineDownloadState(
        downloadProgress: DownloadProgress?,
        playState: PlayState,
        episode: Episode,
    ): DownloadState = when {
        episode.downloaded -> Downloaded(playState is PlayState.Playing && playState.playingId == episode.id)
        downloadProgress?.isComplete() == false -> InProgress(downloadProgress)
        else -> NotDownloaded
    }

    fun downloadEpisode() {
        val ep = episode.value.apply { if (downloaded) return }
        viewModelScope.launch {
            repository.downloadEpisode(ep).collect(_downloadProgress)
        }
    }

    fun deleteEpisode() {
        val ep = episode.value.apply { if (downloaded) return }
        viewModelScope.launch { repository.deleteDownload(ep) }
    }

    fun playEpisode() {
        // TODO: should i add the below line back?
        // if (downloadState.value !is Downloaded) return
        val ep = episode.value
        val uri = repository.getEpisodeUri(ep) ?: return
        viewModelScope.launch { playManager.play(ep.id, uri) }
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
                    application.playManager,
                )
            }
        }
    }
}

sealed interface DownloadState
data object NotDownloaded : DownloadState
data class Downloaded(val playing: Boolean) : DownloadState
data class InProgress(val downloadProgress: DownloadProgress) : DownloadState
