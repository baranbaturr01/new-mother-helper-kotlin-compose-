package com.baranbatur.newmotherhelper.navigation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.baranbatur.newmotherhelper.screens.AboutBabyScreen
import com.baranbatur.newmotherhelper.screens.CategoryListScreen
import com.baranbatur.newmotherhelper.screens.HomeScreen
import com.baranbatur.newmotherhelper.screens.LoginScreen
import com.baranbatur.newmotherhelper.screens.ReceivedScreen
import com.baranbatur.newmotherhelper.screens.RegisterScreen
import com.baranbatur.newmotherhelper.screens.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("received") {
            ReceivedScreen(navController)
        }
        composable("categoryList/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: 0
            CategoryListScreen(navController, categoryId)
        }
        composable("about") {
            AboutBabyScreen(navController)
        }

    }
}