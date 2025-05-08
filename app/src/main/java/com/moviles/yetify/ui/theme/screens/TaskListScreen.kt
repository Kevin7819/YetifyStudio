
package com.moviles.yetify.ui.theme.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.moviles.yetify.models.UserTask
import com.moviles.yetify.viewmodel.UserTaskViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.ui.unit.dp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun TaskListScreen(navController: NavController) {
    val viewModel: UserTaskViewModel = viewModel()
    val userTasks by viewModel.userTasks.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserTasks()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF59C0EF))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF59C0EF))
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF59C0EF))
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
            ) {
                Text(
                    text = "Lista de tareas",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Lista de tareas",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Descripción",
                    color =Color(0xFF59C0EF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Estado",
                    color = Color(0xFF59C0EF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = "Editar/\nEliminar",
                    color = Color(0xFF59C0EF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(80.dp)
                )
            }


            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(userTasks) { task ->
                    TaskItem(
                        task = task,
                        onDelete = { viewModel.deleteUserTask(it) },
                        onEdit = { /* Implements navegation screen of edition */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF59C0EF))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    Text(
                        text = "Volver",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: UserTask, onDelete: (Int) -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xFF2AACF3))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = task.description,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )


        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(getStatusBackgroundColor(task.status))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            when (task.status.lowercase()) {
                "completada" -> Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completada",
                    tint = Color.White
                )
                "pendiente" -> Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Pendiente",
                    tint = Color.White
                )
                else -> Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "En proceso",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))


        IconButton(
            onClick = onEdit,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = Color(0xFF2AACF3)
            )
        }
    }
}

@Composable
private fun getStatusBackgroundColor(status: String): Color {
    return when (status.lowercase()) {
        "completada" -> Color(0xFF4CAF50)
        "pendiente" -> Color(0xFFF44336)
        else -> Color(0xFFFF9800)
    }
}

//@Composable
//fun TaskListScreen(navController: NavController) {
//    val viewModel: UserTaskViewModel = viewModel()
//    val userTasks by viewModel.userTasks.collectAsState()
//
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchUserTasks()
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp)
//    ) {
//        Text(
//            text = "Lista de tareas",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(bottom = 24.dp)
//        )
//
//        LazyColumn(
//            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(userTasks) { task ->
//                TaskItem(
//                    task = task,
//                    onDelete = { viewModel.deleteUserTask(it) },
//                    onEdit = { }
//                )
//            }
//        }
//
//        // The task list would go here
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        Button(
//            onClick = { navController.popBackStack() },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Volver")
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//// Individual component to display a task (still incomplete)
//fun TaskItem(task: UserTask, onDelete: (Int) -> Unit, onEdit: () -> Unit) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        onClick = onEdit
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = task.description,
//                    style = MaterialTheme.typography.titleMedium
//                )
//                Text(
//                    text = task.status,
//                    color = when (task.status.lowercase()) {
//                        "completada" -> MaterialTheme.colorScheme.primary
//                        "pendiente" -> MaterialTheme.colorScheme.error
//                        else -> MaterialTheme.colorScheme.onSurface
//                    }
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "Vence: ${task.dueDate}",
//                style = MaterialTheme.typography.bodySmall
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Button(
//                onClick = { onDelete(task.id) },
//                modifier = Modifier.align(Alignment.End),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.errorContainer
//                )
//            ) {
//                Text("Eliminar")
//            }
//        }
//    }
//}