package com.example.pokemon_mvvm.di

import com.example.pokemon_mvvm.data.client.PokemonClient
import com.example.pokemon_mvvm.data.client.PokemonClient.Companion.BASE_URL
import com.example.pokemon_mvvm.data.repository.PokemonRepositoryImpl
import com.example.pokemon_mvvm.domain.repository.PokemonRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokemonRepository(api: PokemonClient): PokemonRepository {
        return PokemonRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun providePokemonClient(): PokemonClient {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokemonClient::class.java)
    }
}