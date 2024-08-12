package com.baranbatur.newmotherhelper.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baranbatur.newmotherhelper.R
import com.baranbatur.newmotherhelper.service.LoginRequest
import com.baranbatur.newmotherhelper.service.LoginResponse
import com.baranbatur.newmotherhelper.service.RetrofitClient
import com.baranbatur.newmotherhelper.ui.theme.Shapes
import com.baranbatur.newmotherhelper.ui.theme.LightGreyColor
import com.baranbatur.newmotherhelper.ui.theme.PrimaryColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val passwordFocusRequester = FocusRequester()
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.systemBars) // Sistem çubukları için padding
            .windowInsetsPadding(WindowInsets.ime) // Klavye için padding
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
                                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Network error${t.message}",
                                    Toast.LENGTH_LONG
                                )
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
    }
}

private fun Context.doLogin() {
    Toast.makeText(
        this, "Something went wrong, try again later!", Toast.LENGTH_SHORT
    ).show()
}

sealed class InputType(
    val label: String,
    val icon: ImageVector,
    val keyboardOptions: KeyboardOptions,
    val visualTransformation: VisualTransformation
) {
    object Name : InputType(
        label = "İsim",
        icon = Icons.Default.Person,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        visualTransformation = VisualTransformation.None
    )

    object Surname : InputType(
        label = "Soyisim",
        icon = Icons.Default.Person,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        visualTransformation = VisualTransformation.None
    )

    object Email : InputType(
        label = "E-Posta",
        icon = Icons.Default.Email,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        visualTransformation = VisualTransformation.None
    )

    object Password : InputType(
        label = "Şifre", icon = Icons.Default.Lock, keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
        ), visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
fun TextInput(
    inputType: InputType,
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester? = null,
    keyboardActions: KeyboardActions
) {

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester ?: FocusRequester()),
        leadingIcon = { Icon(imageVector = inputType.icon, null) },
        label = { Text(text = inputType.label) },
        shape = Shapes.small,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White, // Background color for text field
            focusedIndicatorColor = PrimaryColor, // Primary color for focused indicator
            unfocusedIndicatorColor = LightGreyColor, // Light grey for unfocused indicator
            disabledIndicatorColor = LightGreyColor // Light grey for disabled indicator
        ),
        singleLine = true,
        keyboardOptions = inputType.keyboardOptions,
        visualTransformation = inputType.visualTransformation,
        keyboardActions = keyboardActions
    )
}
