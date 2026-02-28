package com.example.expensetracker.domain.usecase

``````````````````````````````````````````````````````````import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class MonthlySummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val expensesByCategory: Map<String, Double>
)

class GetSummaryUseCase @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase
) {
    operator fun invoke(userId: String): Flow<MonthlySummary> {
        return getExpensesUseCase(userId).map { resource ->
            when (resource) {
                is com.example.expensetracker.utils.Resource.Success -> {
                    val expenses = resource.data
                    val income = expenses.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val expense = expenses.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    val byCategory = expenses
                        .filter { it.type == TransactionType.EXPENSE }
                        .groupBy { it.category.displayName }
                        .mapValues { entry -> entry.value.sumOf { it.amount } }
                    MonthlySummary(income, expense, income - expense, byCategory)
                }
                else -> MonthlySummary(0.0, 0.0, 0.0, emptyMap())
            }
        }
    }
}
