package com.moviles.yetify.ui.theme.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moviles.yetify.models.Book

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.yetify.viewmodel.BookViewModel

@Preview(showSystemUi = true)
@Composable
fun PreviewListBooks(){
    val bookList = listOf(
        Book(id = 1, title = "Caperucita Roja", author = "Charles Perrault", content = null),
        Book(id = 2, title = "¿A qué sabe la luna?", author = "Michael Grejniec", content = null),
        Book(id = 3, title = "La oruga muy hambrienta", author = "Eric Carle", content = null),
        Book(id = 4, title = "El monstruo de colores", author = "Anna Llenas", content = null),
        Book(id = 5, title = "La pequeña oruga glotona", author = "Eric Carle", content = null)
    )
    ShowListBooks(listBook = bookList, onClickBook = {}, onClickSearch = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenListBooks(onClickBack: () -> Unit, onClickBook: (Book) -> Unit){
    val bookviewmodel:BookViewModel = viewModel()
    val listbook by bookviewmodel.listBooks.collectAsState()

    LaunchedEffect(Unit) {
        bookviewmodel.fetchAllBooks()
    }
    ShowListBooks(
        listBook = listbook,
        onClickBook = onClickBook,
        onClickSearch = {search->
            bookviewmodel.getSearchBook(search)
        }
    )
    ShowTopBar(onClickBack = onClickBack,13)
}

@Composable
fun ShowTopBar(
    onClickBack: () -> Unit,
    coins: Int
) {
    val backgroundColor = Color.Transparent
    val iconAndTextColor = Color.White
    val coinIconColor = Color(0xFFF0B03C)
    val coinBackgroundColor = Color(0xFF42A6F5)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(backgroundColor)
            .padding(top = 30.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // buttonBack
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {

                IconButton(onClick = onClickBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = iconAndTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Volver",
                    color = iconAndTextColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            //coins
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(coinBackgroundColor)
                    .padding(vertical = 4.dp, horizontal = 12.dp)
            ) {

                Icon(
                    //painter = painterResource(id = R.drawable.ic_coin), // Reemplaza con tu ID de recurso
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Monedas",
                    tint = coinIconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = coins.toString(),
                    color = iconAndTextColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ShowListBooks(listBook: List<Book>, onClickSearch: (String) -> Unit, onClickBook: (Book) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4EB1CB))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //box to title of view
        Box(modifier = Modifier.fillMaxSize().weight(0.5f),
            contentAlignment = Alignment.Center
        ) {
            Column{
                Text(
                    text = "MIS LECTURAS",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "ES HORA DE LEER",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        //box for show list of book background white
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.5f)
                .wrapContentHeight()
                .padding(horizontal = 5.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Color.White)
                .border(0.dp, Color.LightGray, RoundedCornerShape(40.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ){
            //show list of books
            Column {
                SearchBar(onClickSearch = onClickSearch)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listBook) { book ->
                        BookCard(book, onClickBook = onClickBook)
                    }
                }
            }

            //gradient in the buttom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0f),
                                Color.White.copy(alpha = 1f)
                            ),
                        )
                    )
            ) {}
        }
    }
}

@Composable
fun SearchBar(onClickSearch:(String)->Unit) {
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = {
            Text(
                text = "BUSCAR LIBRO",
                color = Color(0xFF46ADFF),
                style = TextStyle(
                    fontSize = 14.sp,
                    letterSpacing = 2.sp
                )
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color(0xFF2196F3),
                modifier =Modifier.clickable {onClickSearch(searchText)}
            )
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor =  Color(0xFF2196F3),
            focusedContainerColor = Color(0xCFDAE9FF),
            unfocusedContainerColor = Color(0xCFDAE9FF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent

        ),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF008EFF),
                shape = RoundedCornerShape(50)
            )
    )
}

@Composable
fun BookCard(book: Book, onClickBook:(Book)->Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .clickable { onClickBook(book) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF00A7E1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = book.title.uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = book.author,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PROGRESO DE LECTURA",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(2f)
                ){
                    LinearProgressIndicator(
                        progress = { 0.34f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color.Green,
                        trackColor = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ){
                    Row{
                        repeat(3) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "ECO",
                                tint = Color(0xFFFFA500),
                                modifier = Modifier.size(20.dp).padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
