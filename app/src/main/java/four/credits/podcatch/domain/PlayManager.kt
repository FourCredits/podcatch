package four.credits.podcatch.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlayManager {
    fun currentlyPlaying(): StateFlow<PlayState>
    suspend fun play(episode: Episode)
    suspend fun pause()
}

sealed class PlayState(val playingId: Long? = null) {
    data class Playing(val id: Long) : PlayState(id)
    data class Paused(val id: Long) : PlayState(id)
    data object NotStarted : PlayState()
}
