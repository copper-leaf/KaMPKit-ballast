package co.touchlab.kampkit.repository

import co.touchlab.kampkit.DatabaseHelper
import co.touchlab.kampkit.ktor.DogApi
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache

class BreedRepositoryInputHandler(
    private val dbHelper: DatabaseHelper,
    private val dogApi: DogApi,
    private val eventBus: EventBus
) : InputHandler<
    BreedRepositoryContract.Inputs,
    Any,
    BreedRepositoryContract.State> {

    override suspend fun InputHandlerScope<
        BreedRepositoryContract.Inputs,
        Any,
        BreedRepositoryContract.State>.handleInput(
        input: BreedRepositoryContract.Inputs
    ) = when (input) {
        is BreedRepositoryContract.Inputs.ClearCaches -> {
            updateState { BreedRepositoryContract.State() }
        }
        is BreedRepositoryContract.Inputs.Initialize -> {
            val previousState = getCurrentState()

            if (!previousState.initialized) {
                updateState { it.copy(initialized = true) }
                // start observing flows here
                logger.debug("initializing")
                observeFlows(
                    key = "Observe inputs from EventBus",
                    eventBus
                        .observeInputsFromBus<BreedRepositoryContract.Inputs>(),
                )
            } else {
                logger.debug("already initialized")
                noOp()
            }
        }

        is BreedRepositoryContract.Inputs.RefreshAllCaches -> {
            // refresh all the caches in this repository
            val currentState = getCurrentState()
            if (currentState.breedsInitialized) {
                postInput(BreedRepositoryContract.Inputs.RefreshBreeds(true))
            }

            Unit
        }

        is BreedRepositoryContract.Inputs.RefreshBreeds -> {
            updateState { it.copy(breedsInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.breeds },
                updateState = { BreedRepositoryContract.Inputs.BreedsUpdated(it) },
                doFetch = {
                    val breedResult = dogApi.getJsonFromApi()
                    logger.info("Breed network result: ${breedResult.status}")
                    val breedList = breedResult.message.keys.sorted().toList()
                    logger.info("Fetched ${breedList.size} breeds from network")

                    if (breedList.isNotEmpty()) {
                        dbHelper.insertBreeds(breedList)
                    }
                },
                observe = dbHelper.selectAllItems(),
            )
        }
        is BreedRepositoryContract.Inputs.BreedsUpdated -> {
            updateState { it.copy(breeds = input.value) }
        }
    }
}
