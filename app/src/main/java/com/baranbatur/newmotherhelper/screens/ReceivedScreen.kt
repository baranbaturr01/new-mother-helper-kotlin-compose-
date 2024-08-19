package com.baranbatur.newmotherhelper.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.baranbatur.newmotherhelper.components.BottomNavigationBar
import com.baranbatur.newmotherhelper.components.BottomNavigationBarWtihAds
import com.baranbatur.newmotherhelper.components.InterstitialAdManager
import com.baranbatur.newmotherhelper.components.showCustomToast
import com.baranbatur.newmotherhelper.service.CategoryListData
import com.baranbatur.newmotherhelper.service.CategoryListItem
import com.baranbatur.newmotherhelper.service.DeleteUserCategoryListResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import com.baranbatur.newmotherhelper.service.UpdateCategoryItemRequest
import com.baranbatur.newmotherhelper.service.UpdateCategoryItemResponse
import com.baranbatur.newmotherhelper.service.UserCategoryListData
import com.baranbatur.newmotherhelper.service.UserCategoryListResponse
import com.baranbatur.newmotherhelper.ui.theme.WhiteColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceivedScreen(navController: NavController, token: String) {
    var items by remember { mutableStateOf<List<UserCategoryListData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        RetrofitClient.instance.getUserCategoryList("Bearer $token")
            .enqueue(object : Callback<UserCategoryListResponse> {
                override fun onResponse(
                    call: Call<UserCategoryListResponse>,
                    response: Response<UserCategoryListResponse>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        items = response.body()?.data ?: emptyList()
                    } else {
                        Toast.makeText(context, "Failed to load category items", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<UserCategoryListResponse>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Aldıklarım",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                            color = WhiteColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondary)
            )
        },
        bottomBar = {
            BottomNavigationBarWtihAds(navController = navController)
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
                        .fillMaxSize()
                        .wrapContentSize()
                        .padding(16.dp)
                )
            } else if (items.isEmpty()) {
                EmptyPage()
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { item ->
                        UserCategoryListItemRow(
                            item = item,
                            token = token,
                            navController = navController,
                            onItemDeleted = {
                                items = items.filter { it.id != item.id }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun UserCategoryListItemRow(
    item: UserCategoryListData,
    token: String,
    navController: NavController,
    onItemDeleted: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFDDDDDD), shape = RoundedCornerShape(16.dp)),
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
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Item",
            tint = Color.White,
            modifier = Modifier
                .padding(end = 16.dp)
                .clickable { showDialog = true }
        )
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Bu Ürün Alınmadı Mı?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        deleteItem(item.id, token, context, onItemDeleted)
                    }) {
                        Text("Evet")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Hayır")
                    }
                }
            )
        }
    }
}

fun deleteItem(itemId: Int, token: String, context: Context, onItemDeleted: () -> Unit) {
    InterstitialAdManager.showInterstitialAd(context)
    RetrofitClient.instance.deleteUserCategoryList("Bearer $token", itemId)
        .enqueue(object : Callback<DeleteUserCategoryListResponse> {
            override fun onResponse(
                call: Call<DeleteUserCategoryListResponse>,
                response: Response<DeleteUserCategoryListResponse>
            ) {
                if (response.isSuccessful) {
                    showCustomToast(context, "Ürün Başarıyla Silindi")
                    onItemDeleted()
                } else {
                    showCustomToast(context, "Ürün Silinirken Bir Hata Oluştu")
                }
            }

            override fun onFailure(call: Call<DeleteUserCategoryListResponse>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
}

@Composable
fun EmptyPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Henüz bir şey alınmadı mı?",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}