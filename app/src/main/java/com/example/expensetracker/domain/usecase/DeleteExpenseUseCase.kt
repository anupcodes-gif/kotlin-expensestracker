package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.utils.Resource
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expenseId: String, userId: String): Resource<Unit> {
        return repository.deleteExpense(expenseId, userId)
    }
}
