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
) : ViewModel() {
    val episode = savedStateHandle.getStateFlow(IdArg, 0L)
        .flatMapLatest(repository::getEpisodeById)
        .filterIsInstance<Episode>()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), loading)

    private val _downloadState = MutableStateFlow<DownloadState>(NotDownloaded)
    val downloadState = _downloadState.asStateFlow()

    init {
        viewModelScope.launch {
            episode.map { it.isDownloaded() }.collect(_downloadState)
        }
    }

    private fun Episode.isDownloaded() =
        if (downloaded) Downloaded else NotDownloaded

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
data class InProgress(val downloadProgress: DownloadProgress) : DownloadState
