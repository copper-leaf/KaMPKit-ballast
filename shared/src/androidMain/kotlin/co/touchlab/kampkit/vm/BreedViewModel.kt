package co.touchlab.kampkit.vm

import co.touchlab.kampkit.repository.BreedRepository
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.forViewModel

class BreedViewModel(
    breedRepository: BreedRepository,
    configBuilder: BallastViewModelConfiguration.Builder,
) : AndroidViewModel<
    BreedContract.Inputs,
    BreedContract.Events,
    BreedContract.State
    >(
    config = configBuilder
        .forViewModel(
            initialState = BreedContract.State(),
            inputHandler = BreedInputHandler(breedRepository),
            name = "Breeds",
        )
)
