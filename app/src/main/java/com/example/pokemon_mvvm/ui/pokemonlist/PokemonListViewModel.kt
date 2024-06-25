package com.example.pokemon_mvvm.ui.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.pokemon_mvvm.data.remote.Result
import com.example.pokemon_mvvm.domain.model.PokemonBasicInformation
import com.example.pokemon_mvvm.domain.repository.PokemonRepository
import com.example.pokemon_mvvm.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository,
) : ViewModel() {

    private var currentPage = 0
    var pokemonList = mutableStateOf<List<PokemonBasicInformation>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var cachedPokemonList = listOf<PokemonBasicInformation>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)


    init {
        getPokemonsPaginated()
    }

    fun getPokemonsPaginated() {
        viewModelScope.launch {
            isLoading.value = true

            when (val response = repository.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)) {
                is Resource.Success -> {
                    endReached.value = response.data?.results?.isEmpty() ?: true
                    val pokemonBasicInformationList =
                        response.data?.results?.mapIndexed { _, result ->
                            val pokemonId = extractPokemonIdFromResultUrl(result)
                            PokemonBasicInformation(
                                id = pokemonId.toInt(),
                                name = result.name,
                                imageUrl = getPokemonUrlImage(pokemonId.toInt())
                            )
                        }
                    currentPage++
                    isLoading.value = false
                    pokemonList.value += pokemonBasicInformationList ?: listOf()
                }

                is Resource.Error -> {
                    loadError.value = response.message ?: DEFAULT_ERROR_MESSAGE
                    isLoading.value = false

                }
            }
        }
    }

    private fun extractPokemonIdFromResultUrl(result: Result) =
        if (result.url.endsWith("/")) {
            result.url.dropLast(1).takeLastWhile { it.isDigit() }
        } else {
            result.url.takeLastWhile { it.isDigit() }
        }

    private fun getPokemonUrlImage(pokemonId: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"
    }

    fun calculatePredominantColorByDrawable(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bitmap).generate { palette ->
            val color = palette?.dominantSwatch?.rgb?.let {
                Color(it)
            } ?: Color.White
            onFinish(color)
        }
    }

    fun searchPokemonList(query: String) {
        if (query.isEmpty()) {
            pokemonList.value = cachedPokemonList
            isSearching.value = false
            return
        }

        if (isSearchStarting) {
            cachedPokemonList = pokemonList.value
            isSearchStarting = false
        }

        val filteredList = cachedPokemonList.filter {
            it.name.contains(query, ignoreCase = true)
        }
        pokemonList.value = filteredList
        isSearching.value = false
    }

    companion object {
        const val PAGE_SIZE = 20
        const val DEFAULT_ERROR_MESSAGE = "An unexpected error occurred"
    }
}