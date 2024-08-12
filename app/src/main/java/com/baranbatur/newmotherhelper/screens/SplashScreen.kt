package com.baranbatur.newmotherhelper.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.baranbatur.newmotherhelper.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = "Splash Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Resim alanını doldurmak için kullanılır
        )
    }
    // Splash ekranında birkaç saniye bekle ve sonra ana ekrana yönlendir
    LaunchedEffect(Unit) {
        delay(2000) // 2 saniye bekler
        navController.navigate("login") { // login_screen yerine uygulamanızın ilk ekranını belirtin
            popUpTo("splash_screen") { inclusive = true }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    // Preview fonksiyonu için NavController'a ihtiyaç duyulmaz
    SplashScreen(navController = NavController(LocalContext.current))
}