package co.touchlab.kampkit.vm

import co.touchlab.kampkit.repository.BreedRepository
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import kotlinx.coroutines.flow.map

class BreedInputHandler(
    private val breedRepository: BreedRepository,
) : InputHandler<
    BreedContract.Inputs,
    BreedContract.Events,
    BreedContract.State> {

    override suspend fun InputHandlerScope<
        BreedContract.Inputs,
        BreedContract.Events,
        BreedContract.State>.handleInput(
        input: BreedContract.Inputs
    ) = when (input) {
        is BreedContract.Inputs.RefreshBreeds -> {
            observeFlows(
                "Observe Breeds",
                breedRepository
                    .getBreeds(input.forceRefresh)
                    .map { BreedContract.Inputs.BreedsUpdated(it) },
            )
        }
        is BreedContract.Inputs.BreedsUpdated -> {
            updateState { it.copy(breeds = input.breeds) }
        }
        is BreedContract.Inputs.UpdateBreedFavorite -> {
            breedRepository.updateBreedFavorite(input.breed)
            noOp()
        }
    }
}
