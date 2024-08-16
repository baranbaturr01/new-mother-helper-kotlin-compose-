package com.baranbatur.newmotherhelper.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.baranbatur.newmotherhelper.R
import com.baranbatur.newmotherhelper.components.BottomNavigationBar
import com.baranbatur.newmotherhelper.components.Header
import com.baranbatur.newmotherhelper.service.Category
import com.baranbatur.newmotherhelper.service.CategoryResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import com.baranbatur.newmotherhelper.ui.theme.WhiteColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun HomeScreen(navController: NavController, token: String) {
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        RetrofitClient.instance.getCategories("Bearer $token")
            .enqueue(object : Callback<CategoryResponse> {
                override fun onResponse(
                    call: Call<CategoryResponse>, response: Response<CategoryResponse>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        categories = response.body()?.data ?: emptyList()
                    } else {
                        println(response.errorBody()?.string())
                        Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    Scaffold(
        topBar = { Header() },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories) { category ->
                        CategoryItem(category, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.secondary
    val icons = listOf(
        R.drawable.yeni1,
        R.drawable.yeni2,
        R.drawable.yeni3,
        R.drawable.yeni4,
        R.drawable.yeni5,
        R.drawable.yeni6,
        R.drawable.yeni7,
        R.drawable.yeni8,
        R.drawable.yeni9,
        R.drawable.yeni10,
        R.drawable.yeni11,
        R.drawable.yeni12,
        R.drawable.yeni13,
        R.drawable.yeni14,
        R.drawable.yeni15,

        )
    val icon = remember {
        icons.random()
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Kartların kare olmasını sağlamak için
            .clickable {
                navController.navigate("categoryList/${category.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = MaterialTheme.shapes.medium // Kartların köşelerini yuvarlak yapalım
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween, // İçerikleri dikeyde dağıtalım
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = icon),
                colorFilter = ColorFilter.tint(WhiteColor),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)// İkon boyutunu ayarlayalım
                    .padding(bottom = 8.dp)
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                color = WhiteColor, // Kategorinin adını beyaz renkte yapalım
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodySmall,
                color = WhiteColor.copy(alpha = 1f), // Açıklama metnini biraz daha şeffaf yapalım
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
        }
    }
}