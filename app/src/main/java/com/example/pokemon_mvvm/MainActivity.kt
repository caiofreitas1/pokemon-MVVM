package com.example.pokemon_mvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokemon_mvvm.ui.pokemondetails.PokemonDetailsScreen
import com.example.pokemon_mvvm.ui.pokemonlist.PokemonListScreen
import com.example.pokemon_mvvm.ui.theme.PokemonmvvmTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonmvvmTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "pokemon_list") {
                    composable("pokemon_list") {
                        PokemonListScreen(navController = navController)
                    }
                    composable(
                        route = "pokemon_details/{pokemonName}/{predominantColor}",
                        arguments = listOf(
                            navArgument("pokemonName") {
                                type = NavType.StringType
                            },
                            navArgument("predominantColor") {
                                type = NavType.IntType
                            }
                        )
                    ) { backStackEntry ->
                        val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
                        val predominantColor = backStackEntry.arguments?.getInt("predominantColor") ?: 0
                        PokemonDetailsScreen(
                            navController = navController,
                            pokemonName = pokemonName,
                            predominantColor = Color(predominantColor)
                        )
                    }
                }
            }
        }
    }
}
