package com.baranbatur.newmotherhelper.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.baranbatur.newmotherhelper.ui.theme.LightGreyColor
import com.baranbatur.newmotherhelper.ui.theme.PrimaryColor
import com.baranbatur.newmotherhelper.ui.theme.SecondaryColor
import com.baranbatur.newmotherhelper.ui.theme.WhiteColor

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colorScheme.secondary,
        contentColor = WhiteColor
    ) {
        val screens = listOf(
            Screen.Home,
            Screen.Received,
            Screen.Profile
        )
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = screen.title, tint = WhiteColor) },
                label = { Text(screen.title, color = WhiteColor) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                selectedContentColor = SecondaryColor,
                unselectedContentColor = WhiteColor,
                alwaysShowLabel = true
            )
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Anasayfa", Icons.Filled.Home)
    object Received : Screen("received", "Aldıklarım", Icons.Filled.CheckCircle)
    object Profile : Screen("profile", "Profilim", Icons.Filled.Person)
}
