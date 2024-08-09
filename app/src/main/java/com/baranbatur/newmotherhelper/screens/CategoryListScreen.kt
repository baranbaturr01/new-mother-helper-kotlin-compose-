package com.baranbatur.newmotherhelper.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baranbatur.newmotherhelper.service.CategoryListItem
import com.baranbatur.newmotherhelper.service.CategoryListResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(navController: NavController, categoryId: Int, token: String) {
    var items by remember { mutableStateOf<List<CategoryListItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(categoryId) {
        RetrofitClient.instance.getCategoryList("Bearer $token", categoryId)
            .enqueue(object : Callback<CategoryListResponse> {
                override fun onResponse(
                    call: Call<CategoryListResponse>, response: Response<CategoryListResponse>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        items = response.body()?.data?.items ?: emptyList()
                    } else {
                        Toast.makeText(context, "Failed to load category items", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<CategoryListResponse>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Kategori Listesi") },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Geri Dön")
                }
            }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.itemName)
                            if (item.is_added) {
                                Checkbox(checked = true, onCheckedChange = null)
                            }
                        }
                    }
                }
            }
        }
    }
}