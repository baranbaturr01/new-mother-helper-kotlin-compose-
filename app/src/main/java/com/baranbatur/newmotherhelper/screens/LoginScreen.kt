package com.baranbatur.newmotherhelper.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baranbatur.newmotherhelper.R
import com.baranbatur.newmotherhelper.components.BannerAdView
import com.baranbatur.newmotherhelper.components.InputType
import com.baranbatur.newmotherhelper.components.TextInput
import com.baranbatur.newmotherhelper.service.LoginRequest
import com.baranbatur.newmotherhelper.service.LoginResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import com.baranbatur.newmotherhelper.ui.theme.Shapes
import com.baranbatur.newmotherhelper.ui.theme.PrimaryColor
import com.baranbatur.newmotherhelper.components.showCustomToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val passwordFocusRequester = FocusRequester()
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    LaunchedEffect(Unit) {
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            navController.popBackStack()
            navController.navigate("home")
        }
    }
    BannerAdView(context = context)
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
            .windowInsetsPadding(WindowInsets.ime)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            Modifier.size(200.dp),
            tint = PrimaryColor,
        )
        TextInput(
            InputType.Email,
            value = email,
            onValueChange = { email = it },
            keyboardActions = KeyboardActions(onNext = {
                passwordFocusRequester.requestFocus()
            })
        )
        TextInput(
            InputType.Password,
            value = password,
            onValueChange = { password = it },
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                context.doLogin()
            }),
            focusRequester = passwordFocusRequester
        )
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
                    RetrofitClient.instance.login(LoginRequest(email, password))
                        .enqueue(object : Callback<LoginResponse> {
                            override fun onResponse(
                                call: Call<LoginResponse>, response: Response<LoginResponse>
                            ) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    response.body()?.data?.token?.let { token ->
                                        if (token.isNotEmpty()) {
                                            editor.putString("token", token).apply()
                                            navController.popBackStack()
                                            navController.navigate("home")
                                        } else {
                                            navController.navigate("login")
                                        }
                                    } ?: run {
                                        showCustomToast(context, "Giriş Bilgileri Geçersiz")
                                    }
                                } else {
                                    showCustomToast(context, "Giriş Bilgileri Geçersiz.")
                                }
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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
                    backgroundColor = PrimaryColor, // Primary Color for the button background
                    contentColor = Color.White // White for text color
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Giriş Yap",
                        Modifier
                            .padding(vertical = 8.dp)
                            .align(Alignment.Center),
                        color = Color.White
                    )
                }
                Divider(
                    color = Color.White.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 48.dp)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Hesabın yok mu?")
            TextButton(onClick = {
                navController.navigate("register")
            }) {
                Text(text = "Kayıt Ol", color = PrimaryColor) // Primary Color for link text
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        BannerAdView(context = context)
    }

}

private fun Context.doLogin() {
    Toast.makeText(
        this, "Something went wrong, try again later!", Toast.LENGTH_SHORT
    ).show()
}

