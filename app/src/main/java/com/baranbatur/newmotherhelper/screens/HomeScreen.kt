package com.baranbatur.newmotherhelper.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.baranbatur.newmotherhelper.components.BottomNavigationBar
import com.baranbatur.newmotherhelper.service.Category
import com.baranbatur.newmotherhelper.service.CategoryResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

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

    Scaffold(bottomBar = {
        BottomNavigationBar(navController = navController)
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
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
    val backgroundColor = remember {
        Color(Random.nextInt(128), Random.nextInt(128), Random.nextInt(128))
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .height(150.dp)
            .clickable {
                navController.navigate("categoryList/${category.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),

        ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
