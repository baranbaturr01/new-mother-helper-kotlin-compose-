package com.baranbatur.newmotherhelper.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.baranbatur.newmotherhelper.service.LoginResponse
import com.baranbatur.newmotherhelper.service.RegisterRequest
import com.baranbatur.newmotherhelper.service.RegisterResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import com.baranbatur.newmotherhelper.ui.theme.PrimaryColor
import com.baranbatur.newmotherhelper.ui.theme.Shapes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = FocusRequester()

    BannerAdView(context = context)
    Column(
        modifier = Modifier
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
            InputType.Name,
            value = name,
            onValueChange = { name = it },
            keyboardActions = KeyboardActions(onNext = {
                passwordFocusRequester.requestFocus()
            })
        )
        TextInput(
            InputType.Surname,
            value = surname,
            onValueChange = { surname = it },
            keyboardActions = KeyboardActions(onNext = {
                passwordFocusRequester.requestFocus()
            })
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
                context.doRegister()
            }),
            focusRequester = passwordFocusRequester
        )

        Button(
            onClick = {
                isLoading = true
                RetrofitClient.instance.register(RegisterRequest(name, surname, email, password))
                    .enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>, response: Response<RegisterResponse>
                        ) {
                            isLoading = false
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            isLoading = false
                            Toast.makeText(context, "Network error${t.message}", Toast.LENGTH_LONG)
                                .show()
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
                androidx.compose.material3.Text(
                    "Kayıt Ol",
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Text(text = "Hesabın var mı?")
            androidx.compose.material3.TextButton(onClick = {
                navController.navigate("login")
            }) {
                androidx.compose.material3.Text(
                    text = "Giriş Yap",
                    color = PrimaryColor
                ) // Primary Color for link text
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        BannerAdView(context = context)
    }
}

private fun Context.doRegister() {
    Toast.makeText(
        this, "Something went wrong, try again later!", Toast.LENGTH_SHORT
    ).show()
}