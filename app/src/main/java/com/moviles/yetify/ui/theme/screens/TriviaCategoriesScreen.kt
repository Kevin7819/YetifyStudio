package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val categories = listOf(
    "Ciencias" to "science",
    "Historia" to "history",
    "Arte y Literatura" to "arts_and_literature",
    "Música" to "music",
    "Geografía" to "geography",
    "Cultura" to "society_and_culture",
    "Deportes" to "sport_and_leisure",
    "Cultura General" to "general_knowledge",
    "Cine y TV" to "film_and_tv",
    "Comida y Bebida" to "food_and_drink"
)

@Composable
fun TriviaCategoriesScreen(onCategorySelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Elige una categoría", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        categories.forEach { (friendlyName, apiName) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onCategorySelected(apiName) }
            ) {
                Box(modifier = Modifier.padding(24.dp)) {
                    Text(friendlyName)
                }
            }
        }
    }
}