package com.example.expensetracker.domain.repository

import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ExpenseRepository {
    fun getExpenses(userId: String): Flow<Resource<List<Expense>>>
    fun getExpensesByDateRange(userId: String, startDate: Date, endDate: Date): Flow<Resource<List<Expense>>>
    suspend fun addExpense(expense: Expense): Resource<Unit>
    suspend fun updateExpense(expense: Expense): Resource<Unit>
    suspend fun deleteExpense(expenseId: String, userId: String): Resource<Unit>
    suspend fun syncFromFirestore(userId: String)
}
