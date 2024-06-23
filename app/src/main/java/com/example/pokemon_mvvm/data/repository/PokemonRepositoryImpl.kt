package com.example.pokemon_mvvm.data.repository

import com.example.pokemon_mvvm.data.client.PokemonClient
import com.example.pokemon_mvvm.data.remote.PokemonDetails
import com.example.pokemon_mvvm.data.remote.PokemonList
import com.example.pokemon_mvvm.domain.repository.PokemonRepository
import com.example.pokemon_mvvm.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepositoryImpl @Inject constructor(
    private val pokemonClient: PokemonClient
) : PokemonRepository {
    override suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            pokemonClient.getPokemonList(limit, offset)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred.")
        }
        return Resource.Success(response)
    }

    override suspend fun getPokemonDetails(pokemonName: String): Resource<PokemonDetails> {
        val response = try {
            pokemonClient.getPokemonDetails(pokemonName)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred.")
        }
        return Resource.Success(response)
    }
}