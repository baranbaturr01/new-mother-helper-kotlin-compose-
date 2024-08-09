package com.baranbatur.newmotherhelper.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.baranbatur.newmotherhelper.components.BottomNavigationBar


@Composable
fun ReceivedScreen(navController: NavController) {
    Scaffold(topBar = {
        androidx.compose.material.TopAppBar(title = { Text("Alınanlar") })
    }, bottomBar = {
        BottomNavigationBar(navController)
    }) { innerPadding ->
        // Content of ReceivedScreen
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "Alınanlar içeriği burada olacak.")
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(topBar = {
        androidx.compose.material.TopAppBar(title = { Text("Profilim") })
    }, bottomBar = {
        BottomNavigationBar(navController)
    }) { innerPadding ->
        // Content of ProfileScreen
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "Profilim içeriği burada olacak.")
        }
    }
}