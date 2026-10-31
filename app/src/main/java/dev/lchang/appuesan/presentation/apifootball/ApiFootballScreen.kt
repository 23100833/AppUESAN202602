
package dev.lchang.appuesan.presentation.apifootball

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun ApiFootballScreen(
    viewModel: ApiFootballViewModel = viewModel()
) {
    val countries by viewModel.countries.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val favoriteIds = favorites.map { it.id }.toSet()

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Equipos de fútbol por país",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    selectedCountry?.name ?: "Selecciona un país"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = { Text(country.name) },
                        onClick = {
                            viewModel.onCountrySelected(country)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(
                text = "Error: $errorMessage",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = teams,
                    key = { it.team.id }
                ) { wrapper ->

                    val team = wrapper.team
                    val isFavorite = team.id in favoriteIds

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    team.logo
                                ),
                                contentDescription = "Logo de ${team.name}",
                                modifier = Modifier.size(55.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = team.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(text = team.country)

                                Text(
                                    text = "Fundación: ${
                                        team.founded?.toString() ?: "Sin dato"
                                    }"
                                )
                            }

                            IconButton(
                                onClick = {
                                    viewModel.toggleFavorite(wrapper)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription =
                                        if (isFavorite)
                                            "Quitar de favoritos"
                                        else
                                            "Agregar a favoritos",
                                    tint = if (isFavorite)
                                        Color(0xFFFFC107)
                                    else
                                        MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
