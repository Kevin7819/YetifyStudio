package com.moviles.yetify.ui.theme.screens


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.moviles.yetify.R
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun MainMenuScreen(navController: NavController) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CloudBackgroundMain()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color(0xFF4EB1CB)),
                contentAlignment = Alignment.Center
            ) {
                val density = LocalDensity.current
                val maxPx = with(density) { maxWidth.toPx() }

                val snowflakes = remember {
                    List(25) {
                        createSnowflake(maxPx)
                    }
                }

                snowflakes.forEach { snowflake ->
                    FallingSnowflake(snowflake)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.yeti_normal),
                        contentDescription = "Yeti mascota",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tareas",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height((-40).dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CustomIconButtonMain(
                    text = "Añadir nueva\ntarea",
                    icon = Icons.Default.Add,
                    iconPositionMain = IconPositionMain.RIGHT,
                    onClick = { navController.navigate("addTask") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomIconButtonMain(
                    text = "Lista de\ntareas",
                    icon = Icons.Default.List,
                    iconPositionMain = IconPositionMain.RIGHT,
                    onClick = { navController.navigate("taskList") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomIconButtonMain(
                    text = "Calendario\nde tareas",
                    icon = Icons.Default.DateRange,
                    iconPositionMain = IconPositionMain.RIGHT,
                    onClick = { navController.navigate("calendar") }
                )

                Spacer(modifier = Modifier.height(32.dp))

                CustomIconButtonMain(
                    text = "Volver",
                    icon = Icons.Default.ArrowBack,
                    iconPositionMain = IconPositionMain.LEFT,
                    onClick = { navController.popBackStack() }
                )
            }
        }
    }
}

// Model glasses
data class Snowflake(
    val x: Float,
    val size: Float,
    val speed: Float,
    val amplitude: Float,
    val frequency: Float
)

@Composable
fun FallingSnowflake(snowflake: Snowflake) {
    var y by remember { mutableStateOf(-snowflake.size) }
    var xOffset by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (10000 / snowflake.frequency).toInt(),
                easing = LinearEasing
            )
        )
    )

    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        while (true) {
            y += snowflake.speed
            xOffset = sin(phase * snowflake.frequency) * snowflake.amplitude
            with(density) { if (y > 280.dp.toPx()) y = -snowflake.size }
            delay(16)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = snowflake.size,
            center = Offset(snowflake.x + xOffset, y),
            style = Stroke(width = 1f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = snowflake.size * 0.6f,
            center = Offset(snowflake.x + xOffset, y),
            style = Fill
        )
    }
}

fun createSnowflake(maxPx: Float): Snowflake {
    val random = Random.Default
    return Snowflake(
        x = random.nextFloat() * maxPx,
        size = random.nextFloat() * 8 + 4f,
        speed = random.nextFloat() * 2 + 1,
        amplitude = random.nextFloat() * 20 + 10,
        frequency = random.nextFloat() * 0.5f + 0.3f
    )
}


@Composable
fun CloudBackgroundMain() {
    val density = LocalDensity.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        with(density) {
            val cloudColor = Color(0xFF4EB1CB)
            val cloudRadius = 50.dp.toPx()
            val height = size.height
            val width = size.width

            // Cloud letf
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

            // Cloud right inferior derecha
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

enum class IconPositionMain {
    LEFT, RIGHT
}

@Composable
fun CustomIconButtonMain(
    text: String,
    icon: ImageVector,
    iconPositionMain: IconPositionMain,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .widthIn(min = 180.dp, max = 220.dp)
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
            if (iconPositionMain == IconPositionMain.LEFT) {
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

            if (iconPositionMain == IconPositionMain.RIGHT) {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainMenuScreenPreview() {
    MainMenuScreen(navController = rememberNavController())
}