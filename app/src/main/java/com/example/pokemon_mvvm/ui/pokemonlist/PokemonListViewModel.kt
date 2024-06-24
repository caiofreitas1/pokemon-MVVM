package com.example.pokemon_mvvm.ui.pokemonlist

import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon_mvvm.data.remote.Result
import com.example.pokemon_mvvm.domain.model.PokemonBasicInformation
import com.example.pokemon_mvvm.domain.repository.PokemonRepository
import com.example.pokemon_mvvm.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.palette.graphics.Palette as Pallet

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository,
) : ViewModel() {

    private var currentPage = 0
    var pokemonList = mutableStateOf<List<PokemonBasicInformation>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    fun getPokemonsPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val response = repository.getPokemonList(currentPage * PAGE_SIZE, PAGE_SIZE)

            when (response) {
                is Resource.Success -> {
                    endReached.value = response.data?.results?.isEmpty() ?: true
                    val pokemonBasicInformationList =
                        response.data?.results?.mapIndexed { index, result ->
                            val pokemonId = extractPokemonIdFromResultUrl(result)
                            PokemonBasicInformation(
                                id = pokemonId.toInt(),
                                name = result.name,
                                imageUrl = getPokemonUrlImage(pokemonId.toInt())
                            )
                        }
                    currentPage.inc()
                    isLoading.value = false
                    pokemonList.value += pokemonBasicInformationList ?: listOf()
                }

                is Resource.Error -> {
                    loadError.value = response.message ?: "An unexpected error occurred"
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

    fun calcPredominantColorByDrawable(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bitmap = drawable.toBitmap()

        Pallet.from(bitmap).generate { palette ->
            val color = palette?.dominantSwatch?.rgb?.let {
                Color(it)
            } ?: Color.White
            onFinish(color)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}