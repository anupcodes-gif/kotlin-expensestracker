package com.example.expensetracker.domain.model

import java.util.Date

data class Expense(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val category: Category = Category.OTHER,
    val type: TransactionType = TransactionType.EXPENSE,
    val date: Date = Date(),
    val note: String = ""
)

enum class TransactionType {
    INCOME, EXPENSE
}
