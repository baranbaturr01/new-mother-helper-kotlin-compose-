package com.baranbatur.newmotherhelper.components

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.baranbatur.newmotherhelper.R

fun showCustomToast(context: Context, message: String) {
    val inflater = LayoutInflater.from(context)
    val layout: View = inflater.inflate(
        R.layout.custom_toast_layout, // Create a custom layout for the toast
        null
    )

    val textView: TextView = layout.findViewById(R.id.toast_text)
    textView.text = message

    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.show()
}