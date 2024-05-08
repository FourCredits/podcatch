package four.credits.podcatch.presentation.screens.podcast_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import four.credits.podcatch.PodcatchApplication
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.domain.PodcastRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PodcastDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PodcastRepository,
) : ViewModel() {
    private val id = savedStateHandle.getStateFlow(PODCAST_ID_ARG, 0L)

    val podcast = id
        .flatMapLatest(repository::getPodcastById)
        .filterIsInstance<Podcast>()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            loadingPodcast
        )

    fun delete() {
        viewModelScope.launch {
            repository.deletePodcast(podcast.value)
        }
    }

    companion object {
        private val loadingPodcast = Podcast(
            "loading...",
            "loading...",
            "loading...",
            listOf(),
        )

        const val PODCAST_ID_ARG = "podcastId"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PodcatchApplication
                val repository = application.podcastRepository
                val savedStateHandle = createSavedStateHandle()
                PodcastDetailsViewModel(savedStateHandle, repository)
            }
        }
    }
}

