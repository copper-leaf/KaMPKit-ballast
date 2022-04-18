package co.touchlab.kampkit.vm

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

class BreedEventHandler : EventHandler<
    BreedContract.Inputs,
    BreedContract.Events,
    BreedContract.State> {

    override suspend fun EventHandlerScope<
        BreedContract.Inputs,
        BreedContract.Events,
        BreedContract.State>.handleEvent(
        event: BreedContract.Events
    ) {
        // do nothing
    }
}
