package com.baranbatur.newmotherhelper.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.baranbatur.newmotherhelper.components.BottomNavigationBarWtihAds
import com.baranbatur.newmotherhelper.components.InterstitialAdManager
import com.baranbatur.newmotherhelper.service.ContentData
import com.baranbatur.newmotherhelper.service.ContentResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import com.baranbatur.newmotherhelper.ui.theme.WhiteColor
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AboutBabyScreen(navController: NavController, token: String) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE)
    val cachedAbout = sharedPreferences.getString("about", null)
    var items by rememberSaveable { mutableStateOf<List<ContentData>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var expandedItemId by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        if (cachedAbout != null) {
            items = Gson().fromJson(cachedAbout, Array<ContentData>::class.java).toList()
            Log.d("about", "Fetching categories from cache")
            isLoading = false
        } else {
            Log.d("about", "Fetching categories from API")

            RetrofitClient.instance.getContent("Bearer $token")
                .enqueue(object : Callback<ContentResponse> {
                    override fun onResponse(
                        call: Call<ContentResponse>, response: Response<ContentResponse>
                    ) {
                        isLoading = false
                        if (response.isSuccessful) {
                            items = response.body()?.data ?: emptyList()
                            sharedPreferences.edit().putString("about", Gson().toJson(items))
                                .apply()
                            Log.d("ABOUT", "Veriler backend'den alındı ve cache'e kaydedildi")
                        } else {
                            Toast.makeText(context, "Failed to load items", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<ContentResponse>, t: Throwable) {
                        isLoading = false
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
        }

    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Bebeğim Hakkında",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                    color = WhiteColor
                )
            }, colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondary)
        )
    }, bottomBar = {
        BottomNavigationBarWtihAds(navController = navController)
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
                        ExpandableItemRow(item = item,
                            isExpanded = expandedItemId == item.id,
                            onClick = {
                                expandedItemId = if (expandedItemId == item.id) null else item.id
                            })
                    }
                }
            }
        }
    }


}

@Composable
fun ExpandableItemRow(
    item: ContentData, isExpanded: Boolean, onClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 2.dp)
        .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
        .border(1.dp, Color(0xFFDDDDDD), shape = RoundedCornerShape(16.dp))
        .clickable { onClick() }) {
        // Başlık ve buton kısmı
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.rotate(
                    if (isExpanded) 180f else 0f
                )
            )
        }

        // Resim ve açıklama kısmı, sadece genişletildiğinde gösterilir
        if (isExpanded) {
            InterstitialAdManager.showInterstitialAd(context = LocalContext.current)

            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Sabit yükseklik
                    .background(
                        MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp)
                    ) // Aynı arka plan rengi
                    .padding(8.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ) // Scroll edilebilir hale getirme
            ) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                )
            }
        }
    }
}
