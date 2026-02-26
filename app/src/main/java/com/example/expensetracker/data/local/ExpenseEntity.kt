package com.example.expensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import java.util.Date

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val amount: Double,
    val category: String,
    val type: String,
    val date: Long,
    val note: String
) {
    fun toDomain(): Expense = Expense(
        id = id,
        userId = userId,
        title = title,
        amount = amount,
        category = Category.valueOf(category),
        type = TransactionType.valueOf(type),
        date = Date(date),
        note = note
    )

    companion object {
        fun fromDomain(expense: Expense): ExpenseEntity = ExpenseEntity(
            id = expense.id,
            userId = expense.userId,
            title = expense.title,
            amount = expense.amount,
            category = expense.category.name,
            type = expense.type.name,
            date = expense.date.time,
            note = expense.note
        )
    }
}
