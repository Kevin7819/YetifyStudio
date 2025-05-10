
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.moviles.yetify.ui.theme.YetifyTheme
import java.sql.Date
import java.util.Locale

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.compose.rememberNavController



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

import androidx.compose.ui.platform.LocalDensity
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos


@Composable
fun TaskListScreen(navController: NavController) {
    val viewModel: UserTaskViewModel = viewModel()
    val userTasks by viewModel.userTasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var taskSelected by remember { mutableStateOf<UserTask?>(null) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val snowflakes = remember { List(15) { createSnowflaketask(screenWidth.value) } }

    LaunchedEffect(Unit) {
        viewModel.fetchUserTasks()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Clouds
        CloudBackground()

        // snowflakes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            snowflakes.forEach { snowflake ->
                FallingSnowflaketask(snowflaketask = snowflake)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Eaimation snowflakes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFF59C0EF).copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp)
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
                    Text(
                        text = "Descripción",
                        color = Color(0xFF59C0EF),
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

                // List Task
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(userTasks) { task ->
                        TaskItem(
                            task = task,
                            onDelete = { viewModel.deleteUserTask(it) },
                            onEdit = {
                                showDialog = true
                                taskSelected = task
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Buttom Return
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
                onConfirm = { usertasks ->
                    if (taskSelected != null) {
                        viewModel.updateUserTask(usertasks)
                    }
                    viewModel.fetchUserTasks()
                    showDialog = false
                    taskSelected = null
                },
                onDismiss = {
                    showDialog = false
                    viewModel.fetchUserTasks()
                    taskSelected = null
                },
                onDelete = { id ->
                    if (taskSelected != null) {
                        viewModel.deleteUserTask(id)
                    }
                    viewModel.fetchUserTasks()
                    showDialog = false
                    taskSelected = null
                }
            )
        }
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
            val cloudColor = Color(0xFF59C0EF)
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
fun DialogEditTaskUser(task: UserTask?, onConfirm: (UserTask) -> Unit, onDismiss: () -> Unit, onDelete:(id:Int)->Unit) {
    var description by remember { mutableStateOf(TextFieldValue(task?.description ?: "")) }
    var dueDate by remember { mutableStateOf(TextFieldValue(task?.dueDate ?: "")) }
    var status by remember { mutableStateOf(TextFieldValue(task?.status ?: "")) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dueDate = TextFieldValue(formatter.format(Date(millis)))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF1565C0), modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text( text = "Editar Tarea", style = MaterialTheme.typography.headlineSmall, color = Color.White,
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center )
                Spacer(modifier = Modifier.height(16.dp))

                TaskField( label = "Descripción", placeholder = "Descripción", value = description,
                    onValueChange = { description = it }, icon = Icons.Default.Edit)

                TaskField( label = "Fecha vencimiento", placeholder = "Fecha de entrega", value = dueDate,
                    onValueChange = { dueDate = it }, icon = Icons.Default.DateRange,
                    onClick = { showDatePicker = true })

                TaskField( label = "Estado", placeholder = "Estado", value = status,
                    onValueChange = { status = it }, icon = Icons.Default.Info,)

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Center){
                    Button(
                        onClick = {
                            if (description.text.isBlank() || dueDate.text.isBlank() || status.text.isBlank()) {
                                errorMessage = "Todos los campos son obligatorios"
                                return@Button
                            }
                            var usertask =UserTask(id = task?.id,
                                idUser = task?.idUser,
                                idCourse = task?.idCourse,
                                description = description.text,
                                dueDate = dueDate.text,
                                status = status.text
                            )
                            onConfirm(usertask) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Guardar", color = Color(0xFF1565C0))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Button(
                        onClick = {
                            val id = task?.id ?: -1
                            onDelete(id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    TextButton(onClick = onDismiss,modifier = Modifier.height(40.dp)) {
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