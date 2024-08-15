package com.baranbatur.newmotherhelper.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import com.baranbatur.newmotherhelper.ui.theme.LightGreyColor
import com.baranbatur.newmotherhelper.ui.theme.PrimaryColor
import com.baranbatur.newmotherhelper.ui.theme.Shapes


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
