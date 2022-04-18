package co.touchlab.kampkit.vm

import co.touchlab.kampkit.db.Breed
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.copperleaf.ballast.repository.cache.isLoading

object BreedContract {
    data class State(
        val breeds: Cached<List<Breed>> = Cached.NotLoaded(),
    ) {
        val error: String? = null
        val isLoading: Boolean = breeds.isLoading()
        val breedsList: List<Breed> = breeds.getCachedOrEmptyList()
        override fun toString(): String {
            return "State(" +
                "breeds=${breeds::class.simpleName}[${breedsList.size}], " +
                "error=$error, " +
                "isLoading=$isLoading" +
                ")"
        }
    }

    sealed class Inputs {
        data class RefreshBreeds(val forceRefresh: Boolean) : Inputs()
        data class BreedsUpdated(val breeds: Cached<List<Breed>>) : Inputs() {
            override fun toString(): String {
                return "BreedsUpdated(breeds=${breeds::class.simpleName}[${breeds.getCachedOrEmptyList().size}])"
            }
        }
        data class UpdateBreedFavorite(val breed: Breed) : Inputs()
    }

    sealed class Events {

    }
}
