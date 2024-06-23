package com.example.pokemon_mvvm.data.client

import com.example.pokemon_mvvm.data.remote.PokemonDetails
import com.example.pokemon_mvvm.data.remote.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonClient {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(
        @Path("name") name: String,
    ): PokemonDetails

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
}