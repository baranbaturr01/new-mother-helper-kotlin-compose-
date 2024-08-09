package com.baranbatur.newmotherhelper.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baranbatur.newmotherhelper.service.LoginRequest
import com.baranbatur.newmotherhelper.service.LoginResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                RetrofitClient.instance.login(LoginRequest(email, password))
                    .enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(
                            call: Call<LoginResponse>, response: Response<LoginResponse>
                        ) {
                            isLoading = false
                            println(response)
                            if (response.isSuccessful) {
                                response.body()?.data?.token?.let { token ->
                                    // Store the token securely (e.g., SharedPreferences)
                                    editor.putString("token", token).apply()
                                    // Navigate to the home screen
                                    navController.navigate("home")
                                } ?: run {
                                    Toast.makeText(
                                        context, "Invalid credentials", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            isLoading = false
                            Toast.makeText(context, "Network error${t.message}", Toast.LENGTH_LONG)
                                .show()
                            println(t.message)
                        }
                    })
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController.navigate("register")
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}