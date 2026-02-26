package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.utils.Resource
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Resource<Unit> {
        if (expense.title.isBlank()) return Resource.Error("Title cannot be empty")
        if (expense.amount <= 0) return Resource.Error("Amount must be greater than 0")
        return repository.updateExpense(expense)
    }
}
