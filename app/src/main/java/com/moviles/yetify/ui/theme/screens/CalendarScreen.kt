
package com.moviles.yetify.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.yetify.models.UserTask

import com.moviles.yetify.viewmodel.UserTaskViewModel
import java.util.Calendar
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon



@Composable
fun CalendarScreen(navController: NavController) {
    val viewModel: UserTaskViewModel = viewModel()
    val userTasks by viewModel.userTasks.collectAsState()

    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    LaunchedEffect (Unit){
        viewModel.fetchUserTasks()
    }
    calendarUserTask(list = userTasks, localYear = currentYear, localMonth = currentMonth, localDay = currentDay, goBack = {navController.popBackStack()})
}

@Composable
fun calendarUserTask(
    list: List<UserTask>,
    localYear: Int,
    localMonth: Int,
    localDay: Int,
    goBack: () -> Unit
) {
    val dayWeeks    = listOf("Lun","Mar","Mié","Jue","Vie","Sáb","Dom")
    val dayWeeksAux = listOf("Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo")

    var year  by remember { mutableStateOf(localYear) }
    var month by remember { mutableStateOf(localMonth) }
    val today = localDay

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4EB1CB))
            .padding(top = 32.dp),
        verticalArrangement   = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Calendario",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = "${getMonthNameLegacy(year, month)} $year",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))


                Row(modifier = Modifier.fillMaxWidth()) {
                    dayWeeks.forEach { dw ->
                        Text(
                            text = dw,
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.LightGray),
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))


                val firstDayName     = getFirstDayOfWeekOfMonthLegacy(year, month)
                val lastDayThisMonth = getLastDayOfMonthLegacy(year, month)
                val prevMonth  = if (month == 1) 12 else month - 1
                val prevYear   = if (month == 1) year - 1 else year
                val lastDayPrevMonth = getLastDayOfMonthLegacy(prevYear, prevMonth)
                val startIndex = dayWeeksAux.indexOf(firstDayName)

                var phase  = 1
                var counter = if (startIndex > 0) lastDayPrevMonth - startIndex + 1 else {
                    phase = 2
                    1
                }

                repeat(6) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        repeat(7) {

                            val (m,y) = when(phase){
                                1 -> prevMonth to prevYear
                                2 -> month to year
                                else -> if(month==12) 1 to year+1 else (month+1) to year
                            }
                            val cal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, y)
                                set(Calendar.MONTH, m - 1)
                                set(Calendar.DAY_OF_MONTH, counter)
                            }
                            val cellDate = cal.time

                            CalendarDayItem(
                                day          = counter,
                                calendarDate = cellDate,
                                localDate    = Date(),
                                tasks        = list,
                                isFromCurrentMonth = (phase == 2),
                                modifier     = Modifier.weight(1f)
                            )


                            val limit = when(phase){
                                1 -> lastDayPrevMonth
                                2 -> lastDayThisMonth
                                else -> Int.MAX_VALUE
                            }
                            if(counter == limit){
                                counter = 1
                                phase++
                            } else {
                                counter++
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        if(month == 1){ month = 12; year-- }
                        else month--
                    }) {
                        Text("Anterior")
                    }
                    Button(onClick = {
                        if(month == 12){ month = 1; year++ }
                        else month++
                    }) {
                        Text("Siguiente")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = goBack) {
                        Text("Regresar")
                    }
                }
            }
        }
    }
}




@Composable
fun CalendarDayItem(
    day: Int,
    calendarDate: Date,
    localDate: Date,
    tasks: List<UserTask>,
    isFromCurrentMonth: Boolean,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = Date()


    val tasksOnThisDate = tasks.filter { task ->
        runCatching { sdf.parse(task.dueDate) }
            .mapCatching { isSameDay(it, calendarDate) }
            .getOrDefault(false)
    }

    val hasCompleted = tasksOnThisDate.any { it.status.equals("Completada", ignoreCase = true) }
    val hasInProgress = tasksOnThisDate.any { it.status.equals("En progreso", ignoreCase = true) }
    val hasPending = tasksOnThisDate.any { it.status.equals("Pendiente", ignoreCase = true) }


    val hasOverdue = tasksOnThisDate.any {
        val taskDate = runCatching { sdf.parse(it.dueDate) }.getOrNull()
        taskDate != null &&
                it.status.lowercase() != "completada" &&
                taskDate.before(today) &&
                !isSameDay(taskDate, today)
    }

    val backgroundColor = when {
        hasOverdue    -> Color(0xFFF44336).copy(alpha = 0.3f)
        hasCompleted  -> Color(0xFF4CAF50).copy(alpha = 0.3f)
        hasInProgress -> Color(0xFFFF9800).copy(alpha = 0.3f)
        hasPending    -> Color(0xFFFFEB3B).copy(alpha = 0.3f)
        isSameDay(calendarDate, localDate) ->
            Color.Blue.copy(alpha = 0.5f)
        else -> Color.White
    }


    val labelText = when {
        hasCompleted  -> "Completada"
        hasInProgress -> "En\nprogreso"
        hasPending    -> "Pendiente"
        else          -> ""
    }


    val textColor = if (isFromCurrentMonth) Color.Black else Color.Gray.copy(alpha = 0.5f)

    Column(
        modifier = modifier
            .height(80.dp)
            .background(backgroundColor)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )

        if (labelText.isNotEmpty()) {
            Text(
                text = labelText,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        if (hasOverdue) {
            Text(
                text = "No hecha",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }
    }
}



fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}





fun getMonthNameLegacy(year: Int, month: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    val monthValue = calendar.get(Calendar.MONTH)
    return when (monthValue) {
        Calendar.JANUARY -> "Enero"
        Calendar.FEBRUARY -> "Febrero"
        Calendar.MARCH -> "Marzo"
        Calendar.APRIL -> "Abril"
        Calendar.MAY -> "Mayo"
        Calendar.JUNE -> "Junio"
        Calendar.JULY -> "Julio"
        Calendar.AUGUST -> "Agosto"
        Calendar.SEPTEMBER -> "Septiembre"
        Calendar.OCTOBER -> "Octubre"
        Calendar.NOVEMBER -> "Noviembre"
        Calendar.DECEMBER -> "Diciembre"
        else -> ""
    }
}

fun getFirstDayOfWeekOfMonthLegacy(year: Int, month: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return when (dayOfWeek) {
        Calendar.SUNDAY -> "Domingo"
        Calendar.MONDAY -> "Lunes"
        Calendar.TUESDAY -> "Martes"
        Calendar.WEDNESDAY -> "Miércoles"
        Calendar.THURSDAY -> "Jueves"
        Calendar.FRIDAY -> "Viernes"
        Calendar.SATURDAY -> "Sábado"
        else -> ""
    }
}

fun getLastDayOfMonthLegacy(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

