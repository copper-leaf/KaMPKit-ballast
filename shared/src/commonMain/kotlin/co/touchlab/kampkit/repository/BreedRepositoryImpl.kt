package co.touchlab.kampkit.repository

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.ktor.DogApi
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BootstrapInterceptor
import com.copperleaf.ballast.plusAssign
import com.copperleaf.ballast.repository.BallastRepository
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.withRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BreedRepositoryImpl(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    eventBus: EventBus,
    dogApi: DogApi,
    private val dbHelper: DatabaseHelper,
) : BallastRepository<
    BreedRepositoryContract.Inputs,
    BreedRepositoryContract.State>(
    coroutineScope = coroutineScope,
    eventBus = eventBus,
    config = configBuilder
        .apply {
            initialState = BreedRepositoryContract.State()
            inputHandler = BreedRepositoryInputHandler(
                dbHelper = dbHelper,
                dogApi = dogApi,
                eventBus = eventBus,
            )
            name = "Breed Repository"

            this += BootstrapInterceptor {
                BreedRepositoryContract.Inputs.Initialize
            }
        }
        .withRepository()
        .build()
), BreedRepository {
    override fun getBreeds(forceRefresh: Boolean): Flow<Cached<List<Breed>>> {
        trySend(BreedRepositoryContract.Inputs.RefreshBreeds(forceRefresh))
        return observeStates()
            .map { it.breeds }
    }

    override suspend fun updateBreedFavorite(breed: Breed) {
        dbHelper.updateFavorite(breed.id, !breed.favorite)
    }
}
