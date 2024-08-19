package com.baranbatur.newmotherhelper.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.baranbatur.newmotherhelper.components.BannerAdView
import com.baranbatur.newmotherhelper.components.BottomNavigationBarWtihAds
import com.baranbatur.newmotherhelper.components.InterstitialAdManager
import com.baranbatur.newmotherhelper.service.CategoryListItem
import com.baranbatur.newmotherhelper.service.CategoryListResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import com.baranbatur.newmotherhelper.service.UpdateCategoryItemRequest
import com.baranbatur.newmotherhelper.service.UpdateCategoryItemResponse
import com.baranbatur.newmotherhelper.ui.theme.WhiteColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(navController: NavController, categoryId: Int, token: String) {
    var items by remember { mutableStateOf<List<CategoryListItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var categoryName by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    LaunchedEffect(categoryId) {
        RetrofitClient.instance.getCategoryList("Bearer $token", categoryId)
            .enqueue(object : Callback<CategoryListResponse> {
                override fun onResponse(
                    call: Call<CategoryListResponse>, response: Response<CategoryListResponse>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        response.body()?.let {
                            items = it.data.items ?: emptyList()
                            categoryName = it.data.categoryName
                            description = it.data.description
                        }
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
            title = {
                Column {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                        color = WhiteColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WhiteColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondary),
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Geri Dön",
                        tint = WhiteColor
                    )
                }
            })
    }, bottomBar = {
        BannerAdView(context = context)
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { item ->
                        CategoryListItemRow(
                            item = item, token = token, navController = navController
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun CategoryListItemRow(
    item: CategoryListItem, token: String, navController: NavController
) {
    var isChecked by remember { mutableStateOf(item.is_added) }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFDDDDDD), shape = RoundedCornerShape(16.dp))
            .clickable {
                RetrofitClient.instance
                    .updateCategoryItem(
                        "Bearer $token", UpdateCategoryItemRequest(item.id)
                    )
                    .enqueue(object : Callback<UpdateCategoryItemResponse> {
                        override fun onResponse(
                            call: Call<UpdateCategoryItemResponse>,
                            response: Response<UpdateCategoryItemResponse>
                        ) {
                            if (response.isSuccessful) {
                                isChecked = !isChecked
                                InterstitialAdManager.showInterstitialAd(context)
                                
                            } else {
                                Toast
                                    .makeText(context, "Failed to update item", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onFailure(
                            call: Call<UpdateCategoryItemResponse>, t: Throwable
                        ) {
                            Toast
                                .makeText(context, "Network error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            },
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Icon(
            painter = rememberAsyncImagePainter(item.iconUrl),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(48.dp),
            tint = Color.White
        )
        Text(
            text = item.itemName,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )

        Checkbox(
            checked = isChecked, onCheckedChange = { checked ->
                RetrofitClient.instance.updateCategoryItem(
                    "Bearer $token", UpdateCategoryItemRequest(item.id)
                ).enqueue(object : Callback<UpdateCategoryItemResponse> {
                    override fun onResponse(
                        call: Call<UpdateCategoryItemResponse>,
                        response: Response<UpdateCategoryItemResponse>
                    ) {
                        if (response.isSuccessful) {
                            isChecked = checked
                        } else {
                            Toast.makeText(context, "Failed to update item", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<UpdateCategoryItemResponse>, t: Throwable) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
            }, modifier = Modifier.padding(end = 16.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = Color.White,
                checkmarkColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}