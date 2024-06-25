package com.example.pokemon_mvvm.ui.pokemondetails

import com.example.pokemon_mvvm.data.remote.PokemonDetails
import com.example.pokemon_mvvm.domain.repository.PokemonRepository
import com.example.pokemon_mvvm.utils.Resource
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pokemon_mvvm.ui.pokemondetails.PokemonDetailsViewModel.Companion.ERROR_MESSAGE
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
@HiltAndroidTest
class PokemonDetailsViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()


    private lateinit var viewModel: PokemonDetailsViewModel
    private val repository: PokemonRepository = mockk()

    @Before
    fun setUp() {
        viewModel = PokemonDetailsViewModel(repository)
    }

    @Test
    fun `getPokemonDetails should update pokemonDetails on success`() = runTest {
        //Mock
        val pokemonName = "pikachu"
        val pokemonDetails = mockk<PokemonDetails>()

        every { pokemonDetails.name } returns pokemonName
        coEvery { repository.getPokemonDetails(pokemonName) } returns Resource.Success(
            pokemonDetails
        )

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        //Run
        viewModel.getPokemonDetails(pokemonName)
        advanceUntilIdle()

        //Assert
        assertEquals(pokemonDetails, viewModel.pokemonDetails.value)
        assertFalse(viewModel.isLoading.value)
        assertEquals("", viewModel.loadError.value)
        coVerify { repository.getPokemonDetails(pokemonName) }
    }

    @Test
    fun `getPokemonDetails should update loadError on error`() = runTest {
        //Mock
        val pokemonName = "invalid"
        val errorMessage = "Pokemon not found"
        coEvery { repository.getPokemonDetails(pokemonName) } returns Resource.Error(
            errorMessage
        )

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        //Run
        viewModel.getPokemonDetails(pokemonName)
        advanceUntilIdle()

        //Assert
        assertNull(viewModel.pokemonDetails.value)
        assertFalse(viewModel.isLoading.value)
        assertEquals(errorMessage, viewModel.loadError.value)
        coVerify { repository.getPokemonDetails(pokemonName) }
    }

    @Test
    fun `getPokemonDetails should handle unexpected error`() = runTest {
        //Mock
        val pokemonName = "invalid"
        coEvery { repository.getPokemonDetails(pokemonName) } returns Resource.Error(ERROR_MESSAGE)

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        //Run
        viewModel.getPokemonDetails(pokemonName)
        advanceUntilIdle()

        //Assert
        assertNull(viewModel.pokemonDetails.value)
        assertFalse(viewModel.isLoading.value)
        assertEquals(ERROR_MESSAGE, viewModel.loadError.value)
        coVerify { repository.getPokemonDetails(pokemonName) }
    }
}