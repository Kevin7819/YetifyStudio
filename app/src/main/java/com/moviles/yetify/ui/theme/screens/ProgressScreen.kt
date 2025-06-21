package com.moviles.yetify.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.moviles.yetify.viewmodel.UserTaskViewModel

@Composable
fun ProgressScreen(
    navController: NavController,
    viewModel: UserTaskViewModel = viewModel()
) {
    val userTasks by viewModel.userTasks.collectAsState()
    val courses by viewModel.courses.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserTasks()
        viewModel.fetchCourses()
    }

    LaunchedEffect(userTasks, courses) {
        Log.i("ProgressScreen", "userTasks.size = ${userTasks.size}")
        Log.i("ProgressScreen", "userTasks = $userTasks")
        Log.i("ProgressScreen", "courses.size = ${courses.size}")
        Log.i("ProgressScreen", "courses = $courses")
    }

    // Tabs: Materias / Actividad
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Materias", "Actividad")

    // Cálculos globales
    val totalTasks = userTasks.size
    val completedTasks = userTasks.count { it.status.equals("Completada", ignoreCase = true) }
    val globalProgress = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0

    // Progreso por materia
    val progressByCourse: Map<String, Int> = courses.associate { course ->
        val tasksForCourse = userTasks.filter { it.idCourse == course.id }
        val completed = tasksForCourse.count { it.status.equals("Completada", ignoreCase = true) }
        val percent = if (tasksForCourse.isNotEmpty()) (completed * 100) / tasksForCourse.size else 0
        course.nameCourse to percent
    }

    // Nota visual
    val grade = when (globalProgress) {
        in 90..100 -> "A+ Excelente"
        in 70..89 -> "B Muy bien"
        in 50..69 -> "C Bien"
        in 30..49 -> "D Mejorable"
        else -> "F Esfuerza más"
    }

    // Simulación de mejora semanal (puedes reemplazar con lógica real)
    val weeklyImprovement = 15 // %

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Mi progreso",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0)
        )
        Text(
            "¡Sigue así, vas muy bien!",
            fontSize = 18.sp,
            color = Color(0xFF1976D2),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mensaje si no hay tareas o cursos
        if (userTasks.isEmpty()) {
            Text("No hay tareas registradas.", color = Color.Red, fontWeight = FontWeight.Bold)
        }
        if (courses.isEmpty()) {
            Text("No hay materias registradas.", color = Color.Red, fontWeight = FontWeight.Bold)
        }

        // Contador de tareas completadas
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "$completedTasks Tareas completadas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1565C0),
                modifier = Modifier.padding(16.dp)
            )
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
            tabTitles.forEachIndexed { i, title ->
                Tab(
                    selected = selectedTab == i,
                    onClick = { selectedTab = i },
                    text = { Text(title, color = if (selectedTab == i) Color(0xFF1565C0) else Color.Gray) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Contenido de los tabs
        if (selectedTab == 0) {
            // Progreso por materia con PieChart
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                progressByCourse.toList().forEachIndexed { idx, (course, percent) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        PieChart(
                            percentage = percent,
                            modifier = Modifier.size(56.dp),
                            color = when (idx % 4) {
                                0 -> Color(0xFF42A5F5) // Azul
                                1 -> Color(0xFF66BB6A) // Verde
                                2 -> Color(0xFFFFA726) // Naranja
                                else -> Color(0xFFAB47BC) // Morado
                            }
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            course,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1976D2),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        } else {
            // Actividad semanal (puedes personalizar)
            Text("Resumen semanal", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            Text(
                "Has mejorado un $weeklyImprovement% respecto a la semana anterior.",
                color = Color(0xFF388E3C)
            )
            Spacer(Modifier.height(24.dp))
            Text("Progreso global", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
            LinearProgressIndicator(
                progress = globalProgress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .padding(vertical = 8.dp),
                color = Color(0xFF42A5F5),
                trackColor = Color(0xFFBBDEFB)
            )
            Text("$globalProgress%", fontSize = 18.sp, color = Color(0xFF1565C0))
        }

        Spacer(Modifier.height(32.dp))

        // Nota visual
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
        ) {
            Text(
                text = grade,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        // Botón volver
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Volver", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun PieChart(
    percentage: Int,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF42A5F5),
    backgroundColor: Color = Color(0xFFBBDEFB)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = true
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = (percentage / 100f) * 360f,
                useCenter = true
            )
        }
        Text(
            "$percentage%",
            color = Color(0xFF1565C0),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}