package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.yetify.R


@Preview(showSystemUi = true)
@Composable
fun previewDialog(){
    ScreenCompleteDialog({},{},{},{})
}

@Composable
fun ScreenCompleteDialog(onClickBack: () -> Unit, onClickHome: () -> Unit = {}, onClickStats: () -> Unit = {}, onClickReset: () -> Unit){
    ShowCompleteDialog(onClickHome=onClickHome, onClickStats=onClickStats,onClickReset= onClickReset)
    ShowTopBar(onClickBack = onClickBack,10)
}

@Composable
fun ShowCompleteDialog(onClickHome: () -> Unit = {}, onClickStats: () -> Unit = {}, onClickReset: () -> Unit){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4DB6D1)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top=50.dp)
                    .weight(1.5f)
                    .background(Color.Transparent),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.yetifylogo),
                    contentDescription = "Logo de Yetify",
                    modifier = Modifier.size(450.dp)
                )
            }

            Box( modifier = Modifier
                .fillMaxWidth()
                .weight(2.5f)
                .wrapContentHeight()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White)
                .border(0.dp, Color.LightGray, RoundedCornerShape(40.dp))
                .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¡FELICIDADES,",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "PEQUEÑO LECTOR!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "HAS COMPLETADO TU",
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "AVENTURA DE LECTURA",
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        //
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 48.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Points
                        Text(
                            text = "¡GANASTE 50",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9500),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "PUNTOS!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9500),
                            textAlign = TextAlign.Center
                        )
                    }
            }
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    //home
                    Button(
                        onClick = { onClickHome() },
                        modifier = Modifier.size(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    //continue
                    Button(
                        onClick = { onClickStats() },
                        modifier = Modifier.size(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Continuar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // reset
                    Button(
                        onClick = { onClickReset() },
                        modifier = Modifier.size(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reiniciar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

}