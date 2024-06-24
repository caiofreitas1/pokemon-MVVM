package com.example.pokemon_mvvm.ui.pokemondetails

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon_mvvm.data.remote.PokemonDetails
import com.example.pokemon_mvvm.domain.repository.PokemonRepository
import com.example.pokemon_mvvm.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
    private val repository: PokemonRepository,
) : ViewModel() {

    var pokemonDetails = mutableStateOf<PokemonDetails?>(null)
    var isLoading = mutableStateOf(false)
    var loadError = mutableStateOf("")

    fun getPokemonDetails(pokemonName: String) {
        viewModelScope.launch {
            isLoading.value = true
            loadError.value = ""

            when (val result = repository.getPokemonDetails(pokemonName)) {
                is Resource.Success -> {
                    pokemonDetails.value = result.data
                }
                is Resource.Error -> {
                    loadError.value = result.message ?: "An unexpected error occurred"
                }
            }

            isLoading.value = false
        }
    }
}
