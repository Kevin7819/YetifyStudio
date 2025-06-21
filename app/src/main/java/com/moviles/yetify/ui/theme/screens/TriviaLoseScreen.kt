package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.yetify.R

@Composable
fun TriviaLoseScreen(navController: NavController, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE57373)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "¡Oh no!",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Fallaste una pregunta.\n¡Inténtalo de nuevo!",
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.yeti_sad),
                contentDescription = "Perdiste",
                modifier = Modifier.size(180.dp)
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Intentar de nuevo", color = Color(0xFFD32F2F), fontSize = 20.sp)
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Elegir otra categoría", color = Color(0xFFD32F2F), fontSize = 20.sp)
            }
        }
    }
}