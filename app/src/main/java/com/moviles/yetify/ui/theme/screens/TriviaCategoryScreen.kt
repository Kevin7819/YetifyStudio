package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class CategoryUI(
    val name: String,
    val id: Int,
    val color: Color,
    val icon: @Composable () -> Unit
)

@Composable
fun TriviaCategoryScreen(navController: NavController) {
    val categories = listOf(
        CategoryUI("Ciencia y Naturaleza", 17, Color(0xFF4EB1CB), { Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF4EB1CB)) }),
        CategoryUI("Historia", 23, Color(0xFFBCAAA4), { Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFFBCAAA4)) }),
        CategoryUI("Animales", 27, Color(0xFF81C784), { Icon(Icons.Default.Pets, contentDescription = null, tint = Color(0xFF81C784)) }),
        CategoryUI("Matemáticas", 19, Color(0xFFFBC02D), { Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFFFBC02D)) }),
        CategoryUI("Música", 12, Color(0xFF9575CD), { Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFF9575CD)) }),
        CategoryUI("Mitología", 20, Color(0xFF8D6E63), { Icon(Icons.Default.AutoStories, contentDescription = null, tint = Color(0xFF8D6E63)) }),
        CategoryUI("Televisión", 14, Color(0xFF90CAF9), { Icon(Icons.Default.Tv, contentDescription = null, tint = Color(0xFF90CAF9)) })
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4EB1CB)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Elige una categoría", color = Color.White, fontSize = 28.sp, modifier = Modifier.padding(16.dp))
        categories.forEach { category ->
            Button(
                onClick = { navController.navigate("trivia/${category.id}") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    category.icon()
                    Spacer(Modifier.width(12.dp))
                    Text(category.name, color = category.color, fontSize = 20.sp)
                }
            }
        }
    }
}