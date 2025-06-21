package com.moviles.yetify.ui.theme.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.moviles.yetify.models.UserTask
import com.moviles.yetify.viewmodel.UserTaskViewModel
import java.util.*
import com.moviles.yetify.models.Course
import kotlinx.coroutines.launch
import android.util.Log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController) {

    // An instance of the ViewModel is obtained to handle task logic
    val viewModel: UserTaskViewModel = viewModel()
    val courses by viewModel.courses.collectAsState()
    val context = LocalContext.current

    // ID of the currently authenticated user
    var userId by remember { mutableStateOf(0) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var dueDate by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // gets the user ID and the available courses
    LaunchedEffect(Unit) {
        userId = viewModel.getCurrentUserId()
        viewModel.fetchCourses()
    }

    fun formatDateToISO(input: String): String {
        val parts = input.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt() - 1 // Calendar usa 0-based months
        val year = parts[2].toInt()

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val isoFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getDefault()

        return isoFormat.format(calendar.time)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCourses()
    }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    //allows you to choose a date
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            dueDate = TextFieldValue(
                String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            )
        },
        year,
        month,
        day
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF59C0EF))
                .padding(24.dp)
                .fillMaxWidth(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showError) {
                Text(
                    text = "Complete todos los campos requeridos",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }


            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Asignatura",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // ComboBox Asignature
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedCourse?.nameCourse ?: "Seleccione un curso",
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            unfocusedLabelColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF59C0EF)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.nameCourse, color = Color.Black) },
                                onClick = {
                                    selectedCourse = course
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TaskField(
                label = "Descripción",
                placeholder = "Descripción",
                value = description,
                onValueChange = { description = it },
                icon = Icons.Default.List
            )

            TaskField(
                label = "Fecha vencimiento",
                placeholder = "Fecha de entrega",
                value = dueDate,
                onValueChange = { dueDate = it },
                icon = Icons.Default.DateRange,
                onClick = {
                    datePickerDialog.show()
                }
            )

            Button(
                onClick = {
                    if (description.text.isBlank() || dueDate.text.isBlank() || selectedCourse == null) {
                        showError = true
                        return@Button
                    }
                    showError = false
                    isLoading = true

                    val formattedDate = formatDateToISO(dueDate.text)
                    val newTask = UserTask(
                        id = 0,
                        description = description.text,
                        status = "Pendiente",
                        dueDate = formattedDate,
                        idUser = userId,
                        idCourse = selectedCourse!!.id
                    )

                    viewModel.addUserTask(
                        userTask = newTask,
                        onSuccess = {
                            scope.launch {
                                //show success message
                                Toast.makeText(
                                    context,
                                    "Tarea creada exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                //navigate back only after completing
                                navController.popBackStack()
                            }
                        },
                        onError = { errorMsg ->
                            scope.launch {
                                isLoading = false
                                Log.e("AddTaskScreen", "Error al crear tarea: $errorMsg") // log
                                Toast.makeText(
                                    context,
                                    "Error al crear tarea",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF2196F3)
                ),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .widthIn(min = 120.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("CREAR TAREA", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
                }
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .widthIn(min = 120.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun TaskField(
    label: String,
    placeholder: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    icon: ImageVector,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF59C0EF),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    if (value.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    BasicTextField(
                        value = value.text,
                        onValueChange = { onValueChange(TextFieldValue(it)) },
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
