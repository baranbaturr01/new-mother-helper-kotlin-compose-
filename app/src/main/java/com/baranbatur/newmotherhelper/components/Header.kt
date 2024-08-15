package com.baranbatur.newmotherhelper.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.baranbatur.newmotherhelper.ui.theme.PrimaryColor
import com.baranbatur.newmotherhelper.ui.theme.WhiteColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header() {
    TopAppBar(
        title = {
            Text(
                "Kategoriler",
                textAlign = TextAlign.Center,
                color = WhiteColor
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondary), // Header arka plan rengi
        modifier = Modifier
            .shadow(elevation = 4.dp)
    )
}