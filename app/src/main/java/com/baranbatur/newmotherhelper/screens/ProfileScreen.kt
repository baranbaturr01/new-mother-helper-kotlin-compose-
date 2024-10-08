package com.baranbatur.newmotherhelper.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baranbatur.newmotherhelper.R
import com.baranbatur.newmotherhelper.components.BannerAdView
import com.baranbatur.newmotherhelper.components.BottomNavigationBarWtihAds
import com.baranbatur.newmotherhelper.service.RetrofitClient
import com.baranbatur.newmotherhelper.ui.theme.Shapes
import com.baranbatur.newmotherhelper.ui.theme.PrimaryColor
import com.baranbatur.newmotherhelper.components.showCustomToast
import com.baranbatur.newmotherhelper.service.UserDelete
import com.baranbatur.newmotherhelper.ui.theme.BackgroundColor
import com.baranbatur.newmotherhelper.ui.theme.SecondaryColor
import com.baranbatur.newmotherhelper.ui.theme.WhiteColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext
    var isLoading by remember { mutableStateOf(false) }
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("token", "") ?: ""
    val editor = sharedPreferences.edit()
    BannerAdView(context = context)
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Profil", style = MaterialTheme.typography.titleLarge, color = WhiteColor
                )
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SecondaryColor
            )
        )
    }, bottomBar = {
        BottomNavigationBarWtihAds(navController)
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .background(BackgroundColor)
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    Modifier.size(200.dp),
                    tint = PrimaryColor,
                )
                Spacer(modifier = Modifier.size(16.dp))
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Button(
                        onClick = {
                            isLoading = true
                            RetrofitClient.instance.deleteUser("Bearer $token")
                                .enqueue(object : Callback<UserDelete> {
                                    override fun onResponse(
                                        call: Call<UserDelete>, response: Response<UserDelete>
                                    ) {
                                        isLoading = false
                                        if (response.isSuccessful) {
                                            editor.remove("token")
                                            editor.apply()
                                            navController.navigate("login")
                                            showCustomToast(context, "Hesabınız başarıyla silindi.")
                                        } else {
                                            showCustomToast(context, "Giriş Bilgileri Geçersiz.")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<UserDelete>, t: Throwable
                                    ) {
                                        isLoading = false
                                        Toast.makeText(
                                            context, "Network error${t.message}", Toast.LENGTH_LONG
                                        ).show()
                                        println(t.message)
                                    }
                                })
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFD32F2F)
                        ),
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Hesabımı Sil",
                                Modifier
                                    .padding(vertical = 8.dp)
                                    .align(Alignment.Center),
                                color = Color.White
                            )
                        }
                        Divider(
                            color = Color.White.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(top = 18.dp)
                        )

                    }
                    Divider(
                        color = Color.White.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Button(
                        onClick = {
                            isLoading = true
                            editor.remove("token")
                            editor.apply()
                            navController.navigate("login")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = Shapes.small,
                        colors = ButtonDefaults.buttonColors(
//                            for exit
                            backgroundColor = Color(0xFF82B484) // Red background color
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Çıkış Yap ",
                                Modifier
                                    .padding(vertical = 8.dp)
                                    .align(Alignment.Center),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


