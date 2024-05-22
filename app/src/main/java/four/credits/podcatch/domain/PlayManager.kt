package four.credits.podcatch.domain

import kotlinx.coroutines.flow.Flow

interface PlayManager {
    fun currentlyPlaying(): Flow<PlayState>
    suspend fun play(id: Long, uri: String)
    suspend fun pause()
}

sealed class PlayState(val playingId: Long? = null) {
    data class Playing(val id: Long) : PlayState(id)
    data class Paused(val id: Long) : PlayState(id)
    data object NotStarted : PlayState()
}
