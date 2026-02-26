package com.example.expensetracker.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.toCurrency(currencySymbol: String = "RS "): String {
    val format = NumberFormat.getNumberInstance(Locale.getDefault())
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return "$currencySymbol${format.format(this)}"
}

fun Date.toDisplayString(): String {
    val sdf = SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault())
    return sdf.format(this)
}

fun Date.toFullString(): String {
    val sdf = SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault())
    return sdf.format(this)
}

fun Date.toMonthYear(): String {
    val sdf = SimpleDateFormat(Constants.DATE_FORMAT_MONTH_YEAR, Locale.getDefault())
    return sdf.format(this)
}

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }

fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
