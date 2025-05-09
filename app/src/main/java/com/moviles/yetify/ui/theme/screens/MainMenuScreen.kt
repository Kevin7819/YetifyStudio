
package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainMenuScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp) //
                .background(Color(0xFF59C0EF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Gestión de Tareas",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(modifier = Modifier.height((-40).dp))


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CustomIconButton(
                text = "Añadir nueva\ntarea",
                icon = Icons.Default.Add,
                iconPosition = IconPosition.RIGHT,
                onClick = { navController.navigate("addTask") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomIconButton(
                text = "Lista de\ntareas",
                icon = Icons.Default.List,
                iconPosition = IconPosition.RIGHT,
                onClick = { navController.navigate("taskList") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomIconButton(
                text = "Calendario\nde tareas",
                icon = Icons.Default.DateRange,
                iconPosition = IconPosition.RIGHT,
                onClick = { navController.navigate("calendar") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomIconButton(
                text = "Volver",
                icon = Icons.Default.ArrowBack,
                iconPosition = IconPosition.LEFT,
                onClick = { navController.popBackStack() }
            )
        }
    }
}



enum class IconPosition {
    LEFT, RIGHT
}

@Composable
fun CustomIconButton(
    text: String,
    icon: ImageVector,
    iconPosition: IconPosition,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .widthIn(min = 180.dp, max = 220.dp)
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF59C0EF),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (iconPosition == IconPosition.LEFT) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            if (iconPosition == IconPosition.RIGHT) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}