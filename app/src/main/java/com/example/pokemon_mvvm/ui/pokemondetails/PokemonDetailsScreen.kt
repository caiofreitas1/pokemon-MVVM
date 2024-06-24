package com.example.pokemon_mvvm.ui.pokemondetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.pokemon_mvvm.ui.theme.PokemonmvvmTheme

@Composable
fun PokemonDetailsScreen(
    navController: NavController,
    pokemonName: String,
    predominantColor: Color,
    viewModel: PokemonDetailsViewModel = hiltViewModel(),
) {
    val pokemonDetails by remember {
        viewModel.pokemonDetails
    }

    val isLoading by remember {
        viewModel.isLoading
    }

    val loadError by remember {
        viewModel.loadError
    }

    LaunchedEffect(key1 = pokemonName) {
        viewModel.getPokemonDetails(pokemonName)
    }

    PokemonmvvmTheme {
        Surface(
            color = predominantColor,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    predominantColor,
                                    Color.White
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else if (loadError.isNotBlank()) {
                        Text(
                            text = "Error: $loadError",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Red
                        )
                    } else {
                        Image(
                            painter = rememberImagePainter(pokemonDetails?.sprites?.front_default),
                            contentDescription = pokemonDetails?.name,
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = pokemonDetails?.name?.replaceFirstChar { it.uppercase() } ?: pokemonName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = predominantColor
                )
                Text(
                    text = "ID: ${pokemonDetails?.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = predominantColor
                )
                Text(
                    text = "Height: ${pokemonDetails?.height} dm",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = predominantColor
                )
                Text(
                    text = "Weight: ${pokemonDetails?.weight} hg",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = predominantColor
                )
                Text(
                    text = "Base Experience: ${pokemonDetails?.base_experience}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = predominantColor
                )
                Text(
                    text = "Abilities:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = predominantColor
                )
                pokemonDetails?.abilities?.forEach { ability ->
                    Text(
                        text = ability.ability.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
                        color = predominantColor
                    )
                }
            }
        }
    }
}
