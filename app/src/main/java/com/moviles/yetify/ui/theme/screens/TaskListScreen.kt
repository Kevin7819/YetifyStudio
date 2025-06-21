package com.moviles.yetify.ui.theme.screens

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.yetify.models.UserTask
import com.moviles.yetify.models.Course
import com.moviles.yetify.viewmodel.UserTaskViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.moviles.yetify.ui.theme.YetifyTheme
import java.sql.Date
import java.util.Locale

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.compose.rememberNavController

import androidx.compose.foundation.Canvas
import androidx.compose.material3.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random
import kotlin.math.cos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavController) {
    // Gets the ViewModel using the viewModel() function
    val viewModel: UserTaskViewModel = viewModel()
    // Collects the status of user tasks as a State
    val userTasks by viewModel.userTasks.collectAsState()
    // Collect the status of the courses as a State.
    val courses by viewModel.courses.collectAsState()

    var selectedFilter by remember { mutableStateOf("Todas") }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var courseExpanded by remember { mutableStateOf(false) }
    // Available options for filtering
    val filterOptions = listOf("Todas", "Pendientes", "Completadas", "No hechas", "En progreso")
    var filterExpanded by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var taskSelected by remember { mutableStateOf<UserTask?>(null) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val snowflakes = remember { List(15) { createSnowflaketask(screenWidth.value) } }

    // Processes the tasks to mark the overdue ones
    val processedTasks = remember(userTasks) {
        userTasks.map { task ->
            if ((task.status.equals("Pendiente", ignoreCase = true) ||
                        task.status.equals("En progreso", ignoreCase = true)) &&
                isTaskOverdue(task.dueDate)) {
                task.copy(status = "No hecha")
            } else {
                task
            }
        }
    }

    // Filter the tasks according to the selected criteria
    val filteredTasks = remember(processedTasks, selectedFilter, selectedCourse) {
        var filtered = filterTasks(processedTasks, selectedFilter)

        if (selectedCourse != null) {
            filtered = filtered.filter { it.idCourse == selectedCourse?.id }
        }

        filtered.also { filtered ->
            Log.d("TaskFilter", "Filtro: $selectedFilter, Curso: ${selectedCourse?.nameCourse}, Tareas mostradas: ${filtered.size}")
        }
    }

    // Effect to load initial data when starting the screen
    LaunchedEffect(Unit) {
        viewModel.fetchUserTasks()
        viewModel.fetchCourses()
    }

    // Effect to update tasks when dialog is closed
    LaunchedEffect(showDialog) {
        if (!showDialog) {
            viewModel.fetchUserTasks()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CloudBackground()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            snowflakes.forEach { flake ->
                FallingSnowflaketask(snowflaketask = flake)
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFF4EB1CB).copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        "Lista de tareas",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // ComboBox to select course
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                // Drop-down menu for courses
                ExposedDropdownMenuBox(
                    expanded = courseExpanded,
                    onExpandedChange = { courseExpanded = !courseExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCourse?.nameCourse ?: "Todos los cursos",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar por curso:") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(courseExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF4EB1CB),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = courseExpanded,
                        onDismissRequest = { courseExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos los cursos") },
                            onClick = {
                                selectedCourse = null // Reset selection
                                courseExpanded = false
                            }
                        )

                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.nameCourse) },
                                onClick = {
                                    selectedCourse = course
                                    courseExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // ComboBox to select status filter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = filterExpanded,
                    onExpandedChange = { filterExpanded = !filterExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFilter,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mostrar tareas por estado:") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(filterExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF4EB1CB),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { filterExpanded = false }
                    ) {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedFilter = option
                                    filterExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Descripción",
                        color = Color(0xFF59C0EF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f))
                    Text("Estado",
                        color = Color(0xFF59C0EF),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(80.dp))
                    Text("Editar/\nEliminar",
                        color = Color(0xFF59C0EF),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(80.dp))
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTasks) { task ->
                        val isOverdue = isTaskOverdue(task.dueDate) &&
                                !task.status.equals("Completada", ignoreCase = true)

                        TaskItem(
                            task = task,
                            onDelete = { if (!isOverdue) viewModel.deleteUserTask(it) },
                            onEdit = {
                                if (!isOverdue) {
                                    taskSelected = task
                                    showDialog = true
                                }
                            },
                            isOverdue = isOverdue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomIconButton(
                    text = "Volver",
                    icon = Icons.Default.ArrowBack,
                    iconPosition = IconPosition.LEFT,
                    onClick = { navController.popBackStack() }
                )
            }
        }

        if (showDialog) {
            DialogEditTaskUser(
                task = taskSelected,
                onConfirm = { updatedTask ->
                    viewModel.updateUserTask(updatedTask)
                    showDialog = false
                    taskSelected = null
                },
                onDismiss = {
                    showDialog = false
                    taskSelected = null
                },
                onDelete = { id ->
                    viewModel.deleteUserTask(id)
                    showDialog = false
                    taskSelected = null
                }
            )
        }
    }
}

// Check if a task is overdue by comparing its due date with the current one.
//Boolean true if task is overdue, false otherwise
fun isTaskOverdue(dueDate: String?): Boolean {
    if (dueDate.isNullOrEmpty()) return false
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dueDateObj = dateFormat.parse(dueDate)
        val currentDate = Date(System.currentTimeMillis())
        dueDateObj.before(currentDate)
    } catch (e: Exception) {
        false
    }
}

//Filters a list of tasks according to the selected status.
fun filterTasks(tasks: List<UserTask>, filter: String): List<UserTask> {
    return when (filter) {
        "Pendientes" -> tasks.filter { it.status.equals("Pendiente", ignoreCase = true) }
        "Completadas" -> tasks.filter { it.status.equals("Completada", ignoreCase = true) }
        "No hechas" -> tasks.filter { it.status.equals("No hecha", ignoreCase = true) }
        "En progreso" -> tasks.filter { it.status.equals("En progreso", ignoreCase = true) }
        else -> tasks // "Todas"
    }
}


@Composable
fun FallingSnowflaketask(snowflaketask: Snowflaketask) {
    var y by remember { mutableStateOf(-snowflaketask.size) }
    var xOffset by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()
    val density = LocalDensity.current
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (10000 / snowflaketask.frequency).toInt(),
                easing = LinearEasing
            )
        )
    )

    LaunchedEffect(Unit) {
        while (true) {
            y += snowflaketask.speed
            xOffset = sin(phase * snowflaketask.frequency) * snowflaketask.amplitude
            if (with(density) { y > 100.dp.toPx() + snowflaketask.size }) y = -snowflaketask.size // reboot
            delay(16)
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // L
    ) {
        //snowflake
        repeat(6) { i ->
            val angle = (i * 60).toDouble()
            drawLine(
                color = Color.White.copy(alpha = 0.9f),
                start = Offset(snowflaketask.x + xOffset, y),
                end = Offset(
                    snowflaketask.x + xOffset + (snowflaketask.size * cos(angle * PI / 180)).toFloat(),
                    y + (snowflaketask.size * sin(angle * PI / 180)).toFloat()
                ),
                strokeWidth = 1.5f
            )
        }
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = snowflaketask.size * 0.3f,
            center = Offset(snowflaketask.x + xOffset, y),
            style = Fill
        )
    }
}


data class Snowflaketask(
    val x: Float,
    val size: Float,
    val speed: Float,
    val amplitude: Float,
    val frequency: Float
)


fun createSnowflaketask(maxPx: Float): Snowflaketask {
    val random = Random.Default
    return Snowflaketask(
        x = random.nextFloat() * maxPx,
        size = random.nextFloat() * 8 + 4f,
        speed = random.nextFloat() * 2 + 1,
        amplitude = random.nextFloat() * 20 + 10,
        frequency = random.nextFloat() * 0.5f + 0.2f
    )
}

@Composable
fun CloudBackground() {
    val density = LocalDensity.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        with(density) {
            val cloudColor = Color(0xFF4EB1CB)
            val cloudRadius = 50.dp.toPx()
            val height = size.height
            val width = size.width


            drawCircle(
                color = cloudColor,
                radius = cloudRadius,
                center = Offset(cloudRadius * 0.7f, height - cloudRadius * 0.4f)
            )
            drawCircle(
                color = cloudColor,
                radius = cloudRadius * 0.8f,
                center = Offset(cloudRadius * 1.6f, height - cloudRadius * 0.6f)
            )
            drawCircle(
                color = cloudColor,
                radius = cloudRadius * 0.6f,
                center = Offset(cloudRadius * 0.4f, height - cloudRadius * 1.2f)
            )
            drawCircle(
                color = cloudColor,
                radius = cloudRadius * 0.4f,
                center = Offset(cloudRadius * 1.9f, height - cloudRadius * 1.0f)
            )


            drawCircle(
                color = cloudColor,
                radius = cloudRadius,
                center = Offset(width - cloudRadius * 0.7f, height - cloudRadius * 0.4f)
            )
            drawCircle(
                color = cloudColor,
                radius = cloudRadius * 0.8f,
                center = Offset(width - cloudRadius * 1.6f, height - cloudRadius * 0.6f)
            )
            drawCircle(
                color = cloudColor,
                radius = cloudRadius * 0.6f,
                center = Offset(width - cloudRadius * 0.4f, height - cloudRadius * 1.2f)
            )
            drawCircle(
                color = cloudColor,
                radius = cloudRadius * 0.4f,
                center = Offset(width - cloudRadius * 1.9f, height - cloudRadius * 1.0f)
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
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4EB1CB),
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

// Component representing an individual task item
@Composable
fun TaskItem(task: UserTask, onDelete: (Int) -> Unit, onEdit: () -> Unit, isOverdue: Boolean) {
    // Row containing all the information of the task
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xFF4EB1CB))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Text with the task descriptionHaz clic para usar esta alternativa
        Text(
            text = task.description,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        // Visual status indicator
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(getStatusBackgroundColor(task.status, isOverdue))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                task.status.equals("Completada", ignoreCase = true) ->
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completada",
                        tint = Color.White
                    )
                isOverdue ->
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Vencida",
                        tint = Color.White
                    )
                task.status.equals("Pendiente", ignoreCase = true) ->
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Pendiente",
                        tint = Color.White
                    )
                else ->
                    Icon(
                        imageVector = Icons.Default.AvTimer,
                        contentDescription = "En progreso",
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
                .background(if (isOverdue) Color.Gray else Color.White),
            enabled = !isOverdue
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = if (isOverdue) Color.LightGray else Color(0xFF4EB1CB)
            )
        }
    }
}

@Composable
private fun getStatusBackgroundColor(status: String, isOverdue: Boolean): Color {
    return when {
        isOverdue -> Color(0xFFF44336)
        status.equals("Completada", ignoreCase = true) -> Color(0xFF4CAF50)
        status.equals("Pendiente", ignoreCase = true) -> Color(0xFFFFC107)
        else -> Color(0xFFFF9800)
    }
}

@Preview(showBackground = true)
@Composable
fun previewDialogEdit(){
    YetifyTheme {
        val task = UserTask(
            id = 1,
            idUser = 1,
            idCourse = 1,
            description = "Nothing",
            dueDate = "2025-12-2",
            status = "Pendiente"
        )

        DialogEditTaskUser(task = task, onConfirm = {}, onDismiss = {}, onDelete = {})
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogEditTaskUser(
    task: UserTask?,
    onConfirm: (UserTask) -> Unit,
    onDismiss: () -> Unit,
    onDelete: (id: Int) -> Unit
) {
    var description by remember { mutableStateOf(TextFieldValue(task?.description ?: "")) }
    var dueDate by remember {
        mutableStateOf(
            TextFieldValue(
                task?.dueDate
                    ?.substringBefore("T")
                    ?: ""
            )
        )
    }

    val statusOptions = listOf("Pendiente", "Completada", "En progreso")
    var expanded by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf(task?.status ?: "") }

    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dueDate = TextFieldValue(fmt.format(Date(millis)))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1565C0),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Editar Tarea",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))


                TaskField(
                    label = "Descripción",
                    placeholder = "Descripción",
                    value = description,
                    onValueChange = { description = it },
                    icon = Icons.Default.Edit
                )
                Spacer(Modifier.height(12.dp))


                TaskField(
                    label = "Fecha vencimiento",
                    placeholder = "Fecha de entrega",
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    icon = Icons.Default.DateRange,
                    onClick = { showDatePicker = true }
                )
                Spacer(Modifier.height(12.dp))


                Text(
                    text = "Estado",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 4.dp, bottom = 4.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = statusText,
                        onValueChange = { /* no-op */ },
                        readOnly = true,
                        label = null,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .padding(top = 0.dp)
                            .clickable { expanded = true },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            disabledContainerColor  = Color.White,
                            disabledTextColor       = Color.Black.copy(alpha = 0.6f),
                            cursorColor             = Color.Black,
                            focusedIndicatorColor   = Color.Black,
                            unfocusedIndicatorColor = Color.Black,
                            disabledIndicatorColor  = Color.Gray,
                            focusedLabelColor       = Color.White,
                            unfocusedLabelColor     = Color.White,
                            disabledLabelColor      = Color.White.copy(alpha = 0.6f)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = Color.Black) },
                                onClick = {
                                    statusText = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))


                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (description.text.isBlank()
                                || dueDate.text.isBlank()
                                || statusText.isBlank()
                            ) {
                                errorMessage = "Todos los campos son obligatorios"
                                return@Button
                            }
                            onConfirm(
                                UserTask(
                                    id = task?.id,
                                    idUser = task?.idUser,
                                    idCourse = task?.idCourse,
                                    description = description.text,
                                    dueDate = dueDate.text,
                                    status = statusText
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Guardar", color = Color(0xFF1565C0))
                    }
                }
                Spacer(Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { onDelete(task?.id ?: -1) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TaskListScreenPreview() {
    TaskListScreen(navController = rememberNavController())
}




