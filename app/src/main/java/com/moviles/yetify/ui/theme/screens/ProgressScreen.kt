package com.moviles.yetify.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (selectedTab == 1) Color(0xFF77BFD7) else Color(0xFFE3F2FD))
    ) {
        // ❄️ Copos de nieve decorativos (solo en Materias)
        if (selectedTab == 0) {
            SnowflakeDecoration()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 1. Encabezado principal
            if (selectedTab == 1) {
                // Botón "Volver" arriba a la izquierda (solo en Actividad)
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Volver",
                        color = Color(0xFF1976D2),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(bottom = 8.dp)
                    )
                }
            }

            Text(
                "Mi progreso",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedTab == 1) Color.White else Color(0xFF1565C0),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                "¡Sigue así, vas muy bien! 🚀",
                fontSize = 18.sp,
                color = if (selectedTab == 1) Color.White else Color(0xFF1976D2),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mensaje si no hay tareas o cursos
            if (userTasks.isEmpty()) {
                Text("No hay tareas registradas.", color = Color.Red, fontWeight = FontWeight.Bold)
            }
            if (courses.isEmpty()) {
                Text("No hay materias registradas.", color = Color.Red, fontWeight = FontWeight.Bold)
            }

            // 🔷 2. Sección de tareas completadas
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (selectedTab == 1) Color.White else Color(0xFFBBDEFB)),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completadas",
                        tint = if (selectedTab == 1) Color(0xFF1976D2) else Color(0xFF43A047),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "$completedTasks Tareas completadas",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedTab == 1) Color(0xFF1976D2) else Color(0xFF1565C0)
                    )
                }
            }

            // 📁 3. Tabs: Materias / Actividad
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                indicator = {},
                divider = {}
            ) {
                tabTitles.forEachIndexed { i, title ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = { selectedTab = i },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == i && selectedTab == 1) Color(0xFFE91E63)
                                else if (selectedTab == i) Color(0xFF1565C0)
                                else Color.White
                            )
                        },
                        modifier = Modifier
                            .background(
                                if (selectedTab == i && selectedTab == 1) Color.White
                                else Color.Transparent
                            )
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Contenido de los tabs
            if (selectedTab == 0) {
                // 🏆 4. Subtítulo: “Progreso por materia”
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Progreso por materia",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        fontSize = 20.sp
                    )
                }

                // Lista de materias con barras de progreso
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    progressByCourse.toList().forEachIndexed { idx, (course, percent) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                course,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1976D2),
                                fontSize = 16.sp
                            )
                            LinearProgressIndicator(
                                progress = percent / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = when (idx % 4) {
                                    0 -> Color(0xFF42A5F5)
                                    1 -> Color(0xFF66BB6A)
                                    2 -> Color(0xFFFFA726)
                                    else -> Color(0xFFAB47BC)
                                },
                                trackColor = Color(0xFFBBDEFB)
                            )
                            Text(
                                "$percent%",
                                fontSize = 14.sp,
                                color = Color(0xFF1565C0),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            } else {
                // 🧮 Distribución de actividades con PieCharts
                Text(
                    "Distribución de actividades",
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(12.dp))

                // Grid dinámico de PieCharts para todos los cursos
                val pieColors = listOf(
                    Color(0xFFE53935), // Rojo
                    Color(0xFFFFB300), // Amarillo
                    Color(0xFF1976D2), // Azul
                    Color(0xFF43A047), // Verde
                    Color(0xFF8E24AA), // Morado
                    Color(0xFF00897B), // Turquesa
                    Color(0xFFFF7043), // Naranja
                    Color(0xFF6D4C41)  // Café
                )
                val courseList = progressByCourse.toList()

                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    courseList.chunked(2).forEachIndexed { rowIdx, rowCourses ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowCourses.forEachIndexed { colIdx, (course, percent) ->
                                PieChart(
                                    percentage = percent,
                                    color = pieColors[(rowIdx * 2 + colIdx) % pieColors.size],
                                    label = course
                                )
                            }
                            // Si la fila tiene solo un elemento, agrega un Spacer para alinear
                            if (rowCourses.size == 1) {
                                Spacer(Modifier.width(80.dp))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // 📈 Resumen semanal + Nota visual
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFB3E5FC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "¡Has mejorado un $weeklyImprovement% respecto a la semana anterior!",
                            color = Color(0xFF1976D2),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        LinearProgressIndicator(
                            progress = globalProgress / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = Color(0xFFE91E63),
                            trackColor = Color(0xFFBBDEFB)
                        )
                        Text(
                            grade,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // 🅰️ 7. Nota visual (solo en Materias)
            if (selectedTab == 0) {
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
            }

            Spacer(Modifier.weight(1f))

            // 🔙 8. Botón “Volver” (solo en Materias)
            if (selectedTab == 0) {
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
    }
}

// PieChart con etiqueta
@Composable
fun PieChart(
    percentage: Int,
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color.LightGray,
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
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Text(
            label,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SnowflakeDecoration() {
    // Simples círculos blancos en las esquinas como copos de nieve visuales
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.25f), CircleShape)
                .align(Alignment.TopStart)
                .offset(x = 8.dp, y = 8.dp)
        )
        Box(
            Modifier
                .size(24.dp)
                .background(Color.White.copy(alpha = 0.18f), CircleShape)
                .align(Alignment.TopEnd)
                .offset(x = (-12).dp, y = 24.dp)
        )
        Box(
            Modifier
                .size(28.dp)
                .background(Color.White.copy(alpha = 0.22f), CircleShape)
                .align(Alignment.BottomStart)
                .offset(x = 16.dp, y = (-16).dp)
        )
        Box(
            Modifier
                .size(20.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-8).dp)
        )
    }
}