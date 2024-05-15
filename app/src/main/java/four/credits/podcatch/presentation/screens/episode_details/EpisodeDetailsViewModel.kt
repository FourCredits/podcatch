package four.credits.podcatch.presentation.screens.episode_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import four.credits.podcatch.PodcatchApplication
import four.credits.podcatch.domain.DownloadProgress
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.EpisodeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: EpisodeRepository,
    private val player: ExoPlayer,
) : ViewModel() {
    val episode = savedStateHandle.getStateFlow(IdArg, 0L)
        .flatMapLatest(repository::getEpisodeById)
        .filterIsInstance<Episode>()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), loading)

    private val _downloadState = MutableStateFlow<DownloadState>(NotDownloaded)
    val downloadState = _downloadState.asStateFlow()

    init {
        viewModelScope.launch {
            episode.map(::isDownloaded).collect(_downloadState)
        }
    }

    private fun isDownloaded(episode: Episode) =
        if (episode.downloaded) Downloaded(NotStarted) else NotDownloaded

    fun downloadEpisode() = viewModelScope.launch {
        if (!episode.value.downloaded)
            repository.downloadEpisode(episode.value).collect {
                _downloadState.emit(InProgress(it))
            }
    }

    fun deleteEpisode() = viewModelScope.launch {
        if (episode.value.downloaded)
            repository.deleteDownload(episode = episode.value)
    }

    fun playEpisode() {
        val downloaded = downloadState.value
        if (downloaded !is Downloaded || downloaded.playState is Playing) return
        if (downloaded.playState is NotStarted) {
            val uri = repository.getEpisodeUri(episode.value) ?: return
            player.setMediaItem(MediaItem.fromUri(uri))
        }
        player.play()
        viewModelScope.launch { _downloadState.emit(Downloaded(Playing)) }
    }

    fun pauseEpisode() {
        val downloaded = downloadState.value
        if (downloaded is Downloaded && downloaded.playState is Playing) {
            player.pause()
            viewModelScope.launch { _downloadState.emit(Downloaded(Paused)) }
        }
    }

    companion object {
        val loading = Episode("loading...", "loading...", "loading...")

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PodcatchApplication
                EpisodeDetailsViewModel(
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
data class Downloaded(val playState: PlayState) : DownloadState
data class InProgress(val downloadProgress: DownloadProgress) : DownloadState


sealed interface PlayState
data object NotStarted : PlayState
data object Playing : PlayState
data object Paused : PlayState
