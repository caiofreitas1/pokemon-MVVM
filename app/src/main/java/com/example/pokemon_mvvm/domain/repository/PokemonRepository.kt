package com.example.pokemon_mvvm.domain.repository

import com.example.pokemon_mvvm.data.remote.PokemonDetails
import com.example.pokemon_mvvm.data.remote.PokemonList
import com.example.pokemon_mvvm.utils.Resource

interface PokemonRepository {

        suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList>

        suspend fun getPokemonDetails(pokemonName: String): Resource<PokemonDetails>
}