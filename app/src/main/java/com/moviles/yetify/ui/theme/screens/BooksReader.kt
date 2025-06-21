package com.moviles.yetify.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.yetify.models.Book
import com.moviles.yetify.viewmodel.BookViewModel


@Preview(showSystemUi = true)
@Composable
fun PreviewReaderBook(){
    val sampleBook = Book(
        id = 1,
        title = "LA CAPERUCITA ROJA",
        author = "Anónimo",
        content = """
                        HABÍA UNA VEZ UNA NIÑITA EN UN PUEBLO, LA
                        MÁS BONITA QUE JAMÁS SE HUBIERA VISTO; SU
                        MADRE ESTABA ENLOQUECIDA CON ELLA Y SU
                        ABUELA MUCHO MÁS TODAVÍA.
                        
                        ESTA BUENA MUJER LE HABÍA MANDADO HACER 
                        UNA CAPERUCITA ROJA Y LE SENTABA TANTO QUE
                        TODOS LA LLAMABAN CAPERUCITA ROJA.

                        UN DÍA SU MADRE, HABIENDO COCINADO UNAS
                        TORTAS, LE DIJO.
                        —ANDA A VER CÓMO ESTÁ TU ABUELA, PUES
                        ME DICEN QUE HA ESTADO ENFERMA; LLÉVALE
                        UNA TORTA Y ESTE TARRITO DE MANTEQUILLA.
                        
                        CAPERUCITA ROJA PARTIÓ EN SEGUIDA A VER
                        A SU ABUELA QUE VIVÍA EN OTRO PUEBLO. AL
                        PASAR POR UN BOSQUE, SE ENCONTRÓ CON EL
                        COMPADRE LOBO, QUE TUVO MUCHAS GANAS
                        DE COMÉRSELA, PERO NO SE ATREVIÓ PORQUE
                        UNOS LEÑADORES ANDABAN POR AHÍ CERCA.
                        
                    """.trimIndent()
    )
    ShowTopBar(onClickBack = {},13)
    ShowReadBook(sampleBook, onComplete = {})

}

@Composable
fun  ScreenBookReader(id:Int,onClickBack:()->Unit){
    val bookviewmodel: BookViewModel = viewModel()
    val book by bookviewmodel.book.collectAsState()

    LaunchedEffect(Unit) {
        bookviewmodel.getBookById(id)
    }

    ShowTopBar(onClickBack = onClickBack,12)
    ShowReadBook(book, onComplete = {})
}

@Composable
fun ShowReadBook(book: Book?, onComplete: () -> Unit) {
    val listState = rememberLazyListState()

    //state of reader

    val isAtEnd by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem != null && lastVisibleItem.index == totalItems - 1
        }
    }



    val contentHeight = remember { mutableStateOf(0) }


    //progress data
    val progress by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            val maxScroll = (contentHeight.value - listState.layoutInfo.viewportSize.height).coerceAtLeast(1)
            (offset.toFloat() / maxScroll).coerceIn(0f, 1f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradiente superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF4EB1CB).copy(alpha = 1f),
                                Color(0xFF4EB1CB).copy(alpha = 0f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column{
                    Text(
                        text = book?.title?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start=40.dp, end=40.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color.Green,
                        trackColor = Color.White
                    )
                }
            }

            // Contenido del libro con scroll
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(top = 175.dp, bottom = 20.dp, start = 10.dp, end = 10.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.Transparent)
                    .padding(20.dp)
                    .fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
                //texto de lectura
                item {
                    Text(
                        text = book?.content ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        modifier = Modifier.onGloballyPositioned {
                            contentHeight.value = it.size.height
                        }
                    )

                }
                //espacio para que las ultimas palabras del texto no queden hasta abajo
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // Gradiente inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0f),
                                Color.White.copy(alpha = 1f)
                            )
                        )
                    )
            )

            // botton to end reading book
            if (isAtEnd) {
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp, start = 10.dp,end=10.dp)
                ) {
                    Text("TERMINAR")
                }
            }
        }
    }
}
