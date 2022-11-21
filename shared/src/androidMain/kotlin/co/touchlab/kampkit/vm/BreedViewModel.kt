package co.touchlab.kampkit.vm

import co.touchlab.kampkit.repository.BreedRepository
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.withViewModel

class BreedViewModel(
    breedRepository: BreedRepository,
    configBuilder: BallastViewModelConfiguration.Builder,
) : AndroidViewModel<
    BreedContract.Inputs,
    BreedContract.Events,
    BreedContract.State
    >(
    config = configBuilder
        .withViewModel(
            initialState = BreedContract.State(),
            inputHandler = BreedInputHandler(breedRepository),
            name = "Breeds",
        )
        .build()
)
