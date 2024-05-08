package four.credits.podcatch.presentation.screens.add_podcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import four.credits.podcatch.PodcatchApplication
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.domain.PodcastRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPodcastViewModel(
    private val podcastRepository: PodcastRepository
): ViewModel() {
    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url
    private val _result = MutableStateFlow<Result>(Result.Nothing)
    val result: StateFlow<Result> = _result

    fun searchUrl() {
        if (url.value.isBlank()) return
        _result.update { Result.Loading }
        viewModelScope.launch {
            _result.update {
                Result.Loaded(podcastRepository.getPodcast(url.value))
            }
        }
    }

    fun clearSearch() = _url.update { "" }

    fun setSearch(newUrl: String) = _url.update { newUrl }

    fun addPodcast() = (result.value as? Result.Loaded)?.let {
        viewModelScope.launch { podcastRepository.addPodcast(it.podcast) }
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PodcatchApplication
                val repository = application.podcastRepository
                AddPodcastViewModel(repository)
            }
        }
    }
}

sealed interface Result {
    data object Nothing : Result
    data object Loading : Result
    data class Loaded(val podcast: Podcast) : Result
}
