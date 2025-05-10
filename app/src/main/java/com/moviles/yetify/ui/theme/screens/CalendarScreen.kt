
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
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
fun calendarUserTask(list: List<UserTask>, localYear:Int, localMonth:Int, localDay:Int, goBack:()->Unit) {
    val dayWeeks = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val dayWeeksAux = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    var year by remember { mutableStateOf<Int>(localYear) }
    var month by remember { mutableStateOf<Int>(localMonth) }
    var day   = localDay
    //back ground
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF59C0EF)),
        verticalArrangement = Arrangement.SpaceBetween) {
        //Title of view
        Box(modifier = Modifier.fillMaxWidth().weight(0.7f), contentAlignment = Alignment.Center) {
            Text(text = "Calendario", textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge, color = Color.White
            )
        }
        //calendar
        Box(modifier = Modifier.fillMaxWidth().weight(2.3f).padding(20.dp).clip(RoundedCornerShape(16.dp))
            .background(Color.White).border(1.dp, Color.LightGray, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "${getMonthNameLegacy(year, month)} $year", textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(),
                    color = Color.Black)
                Spacer(modifier = Modifier.height(20.dp))
                //show day of week
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dayW in dayWeeks ) {
                        Text(text = dayW, color = Color.Black, modifier = Modifier
                                .weight(1f).padding(0.dp).background(Color.LightGray), textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                //show
                val firstDayWeekActMonth = getFirstDayOfWeekOfMonthLegacy(year,month)
                val lastDayActMonth  = getLastDayOfMonthLegacy(year,month)

                val previousMonth = if (month == 0) 12 else month - 1
                val previousYear = if (month == 0) year - 1 else year

                val lastDayBeforeMonth = getLastDayOfMonthLegacy(previousYear, previousMonth)
                val indexFirstDay = dayWeeksAux.indexOf(firstDayWeekActMonth)

                var montAct = 1 // use to know what days is localMonth
                var conteo = 1
                if (indexFirstDay != 0){
                    conteo = lastDayBeforeMonth - indexFirstDay+1
                }else{
                    montAct = 2
                }

                for (i in 0..<6) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (j in 0..<7) {
                            var colorToday = Color.White
                            // today color with gren
                            if (conteo == localDay && montAct == 2 && year == localYear  && month == localMonth ){
                                colorToday = Color.Green
                            }
                            //color to task
//                            Text(text = "${conteo}",
//                                color = Color.Black,
//                                modifier = Modifier.weight(1f).height(60.dp).background(colorToday),
//                                textAlign = TextAlign.Center
//                            )

                            var monthToData = month
                            var yearToDate = year
                            if (montAct == 1){
                                monthToData = if (month == 0) 12 else month - 1
                                yearToDate = if (month == 0) year - 1 else year
                            }
                            if (montAct == 2){
                                monthToData = month
                                yearToDate = year
                            }

                            if(montAct == 3){
                                monthToData = if (month + 1 == 13) 1 else month + 1
                                yearToDate = if (month + 1 == 13) year + 1 else year

                                Log.e("146" , "$monthToData  $yearToDate")
                            }


                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.YEAR, yearToDate)
                            calendar.set(Calendar.MONTH, monthToData - 1)
                            calendar.set(Calendar.DAY_OF_MONTH, conteo)
                            val currentDate = calendar.time

                            CalendarDayItem(
                                day = conteo,
                                calendarDate = currentDate,
                                localDate = Date(),
                                tasks = list,
                                modifier = Modifier.weight(1f)
                            )

                            if ((conteo + 1 > lastDayBeforeMonth && montAct == 1 || conteo + 1 > lastDayActMonth && montAct == 2)) {
                                conteo = 1
                                montAct++
                            }else{
                                conteo++
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        if (month - 1 == 0){
                            month = 12
                            year--
                        }else{
                            month--
                        }
                    }) {
                        Text("Anterior")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(onClick = {
                        if (month + 1  == 13){
                            month = 1
                            year++
                        }else{
                            month++
                        }
                    }) {
                        Text("Siguiente")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = goBack) { Text("Regresar") }
                }

            }
        }
    }
}@Composable
fun CalendarDayItem(
    day: Int,
    calendarDate: Date,
    localDate: Date,
    tasks: List<UserTask>,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    Log.e("203", "$localDate  $calendarDate")
    val taskOnThisDate = tasks.any { task ->
        try {
            val taskDate = sdf.parse(task.dueDate)
            taskDate != null && isSameDay(taskDate, calendarDate)
        } catch (e: Exception) {
            false
        }
    }

    val backgroundColor = when {
        isSameDay(calendarDate, localDate) -> Color.Green
        taskOnThisDate -> Color.Yellow.copy(alpha = 0.3f)
        else -> Color.White
    }

    val statusText = when {
        !taskOnThisDate -> ""
        calendarDate.before(localDate) -> "Vencida"
        isSameDay(calendarDate, localDate) -> "Hoy"
        else -> "Próxima"
    }

    Column(
        modifier = modifier
            .height(60.dp)
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "$day", textAlign = TextAlign.Center, color = Color.Black)
        if (statusText.isNotEmpty()) {
            Text(text = statusText, fontSize = 10.sp, color = Color.DarkGray)
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




