package com.example.pokemon_mvvm.ui.pokemonlist


import com.example.pokemon_mvvm.data.remote.PokemonList
import com.example.pokemon_mvvm.data.remote.Result
import com.example.pokemon_mvvm.domain.repository.PokemonRepository
import com.example.pokemon_mvvm.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PokemonListViewModelTest {
    private lateinit var viewModel: PokemonListViewModel
    private lateinit var repository: PokemonRepository

    @Before
    fun setUp() {
        repository = mockk()
    }

    @Test
    fun `getPokemonsPaginated should update pokemonList on success`() = runTest {
        // Mock
        val resultPokemon = Result(name = "bulbasaur", url = "url/11")
        val pokemonList = PokemonList(
            count = 1,
            next = "2",
            previous = "",
            results = listOf(resultPokemon)

        )
        coEvery { repository.getPokemonList(any(), any()) } returns Resource.Success(pokemonList)

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        // Run
        viewModel = PokemonListViewModel(repository)
        advanceUntilIdle()

        // Assert
        assertEquals(1, viewModel.pokemonList.value.size)
    }

    @Test
    fun `getPokemonsPaginated should update loadError on error`() = runTest {
        // Mock
        coEvery { repository.getPokemonList(any(), any()) } returns Resource.Error("Error")

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        // Run
        viewModel = PokemonListViewModel(repository)
        viewModel.getPokemonsPaginated()
        advanceUntilIdle()

        // Assert
        assertEquals("Error", viewModel.loadError.value)
    }

}
