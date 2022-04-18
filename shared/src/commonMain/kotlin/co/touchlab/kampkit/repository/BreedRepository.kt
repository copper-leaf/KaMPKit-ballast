package co.touchlab.kampkit.repository

import co.touchlab.kampkit.db.Breed
import com.copperleaf.ballast.repository.cache.Cached
import kotlinx.coroutines.flow.Flow

interface BreedRepository {
    fun getBreeds(forceRefresh: Boolean): Flow<Cached<List<Breed>>>

    suspend fun updateBreedFavorite(breed: Breed)
}
