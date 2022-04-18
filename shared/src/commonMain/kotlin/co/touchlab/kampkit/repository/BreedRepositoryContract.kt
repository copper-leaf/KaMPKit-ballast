package co.touchlab.kampkit.repository

import co.touchlab.kampkit.db.Breed
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList

object BreedRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val breedsInitialized: Boolean = false,
        val breeds: Cached<List<Breed>> = Cached.NotLoaded(),
    ) {
        override fun toString(): String {
            return "State(" +
                "initialized=$initialized, " +
                "breedsInitialized=$breedsInitialized, " +
                "breeds=${breeds::class.simpleName}[${breeds.getCachedOrEmptyList().size}]" +
            ")"
        }
    }

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshBreeds(val forceRefresh: Boolean) : Inputs()
        data class BreedsUpdated(val value: Cached<List<Breed>>) : Inputs() {
            override fun toString(): String {
                return "BreedsUpdated(value=${value::class.simpleName}[${value.getCachedOrEmptyList().size}])"
            }
        }
    }
}
