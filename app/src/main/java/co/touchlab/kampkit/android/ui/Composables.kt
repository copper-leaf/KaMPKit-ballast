package co.touchlab.kampkit.android.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.vm.BreedContract
import co.touchlab.kampkit.vm.BreedViewModel
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MainScreen(
    viewModel: BreedViewModel
) {
    val vmState by viewModel.observeStates().collectAsState()

    MainScreenContent(vmState) { viewModel.trySend(it) }
}

@Composable
fun MainScreenContent(
    vmState: BreedContract.State,
    postInput: (BreedContract.Inputs) -> Unit
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = vmState.isLoading),
            onRefresh = { postInput(BreedContract.Inputs.RefreshBreeds(true)) }
        ) {
            val error = vmState.error
            if (error != null) {
                Error(error)
            } else {
                val breeds = vmState.breeds.getCachedOrEmptyList()
                if (breeds.isNotEmpty()) {
                    Success(successData = breeds, postInput = postInput)
                } else {
                    Empty()
                }
            }
        }
    }
}

@Composable
fun Empty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.empty_breeds))
    }
}

@Composable
fun Error(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error)
    }
}

@Composable
fun Success(
    successData: List<Breed>,
    postInput: (BreedContract.Inputs) -> Unit
) {
    DogList(breeds = successData) { postInput(BreedContract.Inputs.UpdateBreedFavorite(it)) }
}

@Composable
fun DogList(breeds: List<Breed>, onItemClick: (Breed) -> Unit) {
    LazyColumn {
        items(breeds) { breed ->
            DogRow(breed) {
                onItemClick(it)
            }
            Divider()
        }
    }
}

@Composable
fun DogRow(breed: Breed, onClick: (Breed) -> Unit) {
    Row(
        Modifier
            .clickable { onClick(breed) }
            .padding(10.dp)
    ) {
        Text(breed.name, Modifier.weight(1F))
        FavoriteIcon(breed)
    }
}

@Composable
fun FavoriteIcon(breed: Breed) {
    Crossfade(
        targetState = !breed.favorite,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    ) { fav ->
        if (fav) {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_border_24px),
                contentDescription = stringResource(R.string.favorite_breed, breed.name)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_24px),
                contentDescription = stringResource(R.string.unfavorite_breed, breed.name)
            )
        }
    }
}

@Preview
@Composable
fun MainScreenContentPreview_Success() {
    MainScreenContent(
        vmState = BreedContract.State(
            breeds = Cached.Value(
                listOf(
                    Breed(0, "appenzeller", false),
                    Breed(1, "australian", true)
                )
            )
        )
    ) { }
}
