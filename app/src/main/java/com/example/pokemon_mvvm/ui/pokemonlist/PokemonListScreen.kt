package com.example.pokemon_mvvm.ui.pokemonlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.example.pokemon_mvvm.R
import com.example.pokemon_mvvm.domain.model.PokemonBasicInformation


@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.pokemon_logo),
                contentDescription = "Pokemon Logo",
                modifier = Modifier
                    .fillMaxWidth(0.7F)
                    .align(CenterHorizontally)

            )
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                hint = "Search Pokemon",
                onSearch = {
                    viewModel.searchPokemonList(it)
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            PokemonList(navController = navController)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search pokemon by name",
    onSearch: (String) -> Unit,
) {
    var text by remember {
        mutableStateOf("")
    }

    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text, onValueChange = {
                text = it
                onSearch(it)
            }, maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = CircleShape)
                .background(color = Color.White, shape = CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged { focusState ->
                    isHintDisplayed = !focusState.isFocused && text.isEmpty()
                }
        )

        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                style = TextStyle(color = Color.LightGray),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.pokeball_image),
            contentDescription = "Pokeball",
            modifier = Modifier
                .size(40.dp)
                .padding(end = 16.dp)
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {
    val pokemonList by remember {
        viewModel.pokemonList
    }

    val endReached by remember {
        viewModel.endReached
    }

    val isLoading by remember {
        viewModel.isLoading
    }

    val isSearching by remember {
        viewModel.isSearching
    }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        val itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }
        items(itemCount) {
            if (it >= itemCount - 1 && !endReached && !isLoading && !isSearching) {
                viewModel.getPokemonsPaginated()
            }
            PokemonRow(rowIndex = it, pokemons = pokemonList, navController = navController)
        }
    }
}

@Composable
fun PokemonBasicCard(
    pokemon: PokemonBasicInformation,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {
    val defaultPredominantColor = Color.DarkGray
    var predominantColor by remember {
        mutableStateOf(defaultPredominantColor)
    }
    var isLoading by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current

    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(Brush.horizontalGradient(listOf(defaultPredominantColor, predominantColor)))
            .clickable {
                navController.navigate(
                    "pokemon_details/${pokemon.name}/${predominantColor.toArgb()}"
                )
            }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(pokemon.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = pokemon.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp)),
                onState = { state ->
                    isLoading = when (state) {
                        is AsyncImagePainter.State.Loading -> true
                        is AsyncImagePainter.State.Success -> {
                            val drawable = state.result.drawable
                            viewModel.calculatePredominantColorByDrawable(drawable) { color ->
                                predominantColor = color
                            }
                            false
                        }
                        else -> false
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "#${pokemon.id} - ${pokemon.name}".uppercase(),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .wrapContentWidth(CenterHorizontally),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White),
                contentAlignment = Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun PokemonRow(
    rowIndex: Int,
    pokemons: List<PokemonBasicInformation>,
    navController: NavController,
) {
    Column {
        Row {
            PokemonBasicCard(
                pokemon = pokemons[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(16.dp))
            if (pokemons.size >= rowIndex * 2 + 2) {
                PokemonBasicCard(
                    pokemon = pokemons[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
