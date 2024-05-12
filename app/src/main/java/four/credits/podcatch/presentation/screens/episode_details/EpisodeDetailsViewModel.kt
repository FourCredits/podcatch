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
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.EpisodeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class EpisodeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: EpisodeRepository,
) : ViewModel() {
    private val id = savedStateHandle.getStateFlow(IdArg, 0L)

    val episode = id
        .flatMapLatest(repository::getEpisodeById)
        .filterIsInstance<Episode>()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), loading)

    private val _downloadState: MutableStateFlow<DownloadState> =
        MutableStateFlow(NotDownloaded)

    val downloadState: StateFlow<DownloadState> = _downloadState

    init {
        viewModelScope.launch {
            episode.collect {
                _downloadState.emit(
                    if (it.downloaded) Downloaded else NotDownloaded
                )
            }
        }
    }

    fun downloadEpisode() = viewModelScope.launch {
        repository.downloadEpisode(
            episode = episode.value,
            onProgressUpdate = { downloaded, total ->
                viewModelScope.launch {
                    _downloadState.emit(InProgress(downloaded, total))
                }
            },
        )
    }

    companion object {
        val loading = Episode("loading...", "loading...", "loading...")

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PodcatchApplication
                val repository = application.episodeRepository
                val savedStateHandle = createSavedStateHandle()
                EpisodeDetailsViewModel(savedStateHandle, repository)
            }
        }
    }
}

sealed interface DownloadState
data object NotDownloaded : DownloadState
data object Downloaded : DownloadState
data class InProgress(val downloadedBytes: Long, val totalBytes: Long) :
    DownloadState
