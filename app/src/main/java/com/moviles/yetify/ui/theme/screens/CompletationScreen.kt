package com.moviles.yetify.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Preview(showSystemUi = true)
@Composable
fun previewDialog(){
    ShowCompleteDialog({},{},{})
}

@Composable
fun ShowCompleteDialog(onClickBack: () -> Unit, onClickHome: () -> Unit = {}, onClickStats: () -> Unit = {},){

}