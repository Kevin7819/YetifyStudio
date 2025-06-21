package com.moviles.yetify.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch


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
                        
                    """.trimIndent(),
        progress = 0.3
    )

    ShowReadBook(sampleBook, onComplete = {}, updatingProgress = {bookId, progress->

    })
    ShowTopBar(onClickBack = {},13)

}

@Composable
fun  ScreenBookReader(id:Int,onClickBack:()->Unit){
    val bookviewmodel: BookViewModel = viewModel()
    val book by bookviewmodel.book.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        bookviewmodel.getBookById(id)
    }

    ShowTopBar(onClickBack = onClickBack,12)
    ShowReadBook(book, onComplete = {}, updatingProgress = {bookId, progress->
        coroutineScope.launch {
            bookviewmodel.updateBookProgress(bookId, progress)
        }
    })
}

@Composable
fun ShowReadBook(book: Book?, onComplete: () -> Unit, updatingProgress: (Int,Double) -> Unit) {
    val listState = rememberLazyListState()

    // state to initial progress
    var hasAppliedInitialProgress by remember { mutableStateOf(false) }

    // calculate hight
    val contentHeight = remember { mutableIntStateOf(0) }

    var lastProgress by remember { mutableStateOf(0.0) }


    // add progress initial to
    LaunchedEffect(book, contentHeight.intValue) {
        if (book != null && contentHeight.intValue > 0 && !hasAppliedInitialProgress) {
            val savedProgress = book.progress.toFloat()

            if (savedProgress > 0f) {
                //calculate the progress
                val viewportHeight = listState.layoutInfo.viewportSize.height
                val maxScroll = (contentHeight.intValue - viewportHeight).coerceAtLeast(1)
                val targetScrollOffset = (savedProgress * maxScroll).toInt()

                // slow scroll
                listState.animateScrollToItem(
                    index = 1, // content index
                    scrollOffset = targetScrollOffset
                )

                hasAppliedInitialProgress = true
            }
        }
    }

    // detect end read book
    val isAtEnd by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem != null && lastVisibleItem.index == totalItems - 1
        }
    }

    // calcaculate current progress
    val currentProgress by remember {
        derivedStateOf {
            if (contentHeight.intValue > 0) {
                val offset = listState.firstVisibleItemScrollOffset
                val viewportHeight = listState.layoutInfo.viewportSize.height
                val maxScroll = (contentHeight.intValue - viewportHeight).coerceAtLeast(1)
                (offset.toFloat() / maxScroll).coerceIn(0f, 1f)
            } else {
                book?.progress?: 0f
            }
        }
    }

    LaunchedEffect(currentProgress) {
        if (currentProgress.toDouble() >= lastProgress + 0.5) {
            book?.let {
                updatingProgress(it.id, currentProgress.toDouble())
                lastProgress = currentProgress.toDouble()
            }
        }
    }
    //save progress when changed screen
    DisposableEffect(Unit) {
        onDispose {
            if (book != null) {
                updatingProgress(book.id,currentProgress.toDouble())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // top gradiant
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
                Column {
                    Text(
                        text = book?.title ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // progress porcents
                    Text(
                        text = "${(currentProgress.toFloat() * 100).toInt()}% completado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { currentProgress.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, end = 40.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color.Green,
                        trackColor = Color.White
                    )
                }
            }

            // content
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(top = 175.dp, bottom = 30.dp, start = 10.dp, end = 10.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.Transparent)
                    .padding(20.dp)
                    .fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }

                // text of read
                item {
                    Text(
                        text = book?.content ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            contentHeight.intValue = coordinates.size.height
                        }
                    )
                }

                // space
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // bottom gradiant
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

            // end reading
            if (isAtEnd) {
                Button(
                    onClick = {
                        if (book != null) {
                            updatingProgress(book.id,1.0)
                        }
                        onComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 30.dp, start = 15.dp, end = 15.dp)
                ) {
                    Text("TERMINAR")
                }
            }
        }
    }
}

@Composable
fun ContinueReadingBadge(progress: Double, modifier: Modifier = Modifier) {
    if (progress > 0.0 && progress < 1.0) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4EB1CB)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Continuar",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Continuar ${(progress * 100).toInt()}%",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
