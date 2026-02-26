package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.remote.FirestoreDataSource
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.repository.ExpenseRepository
import com.example.expensetracker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val localDao: ExpenseDao,
    private val remoteDataSource: FirestoreDataSource
) : ExpenseRepository {

    override fun getExpenses(userId: String): Flow<Resource<List<Expense>>> {
        // Emit from local Room cache (already synced), then also listen to Firestore
        return localDao.getAllExpenses(userId).map { entities ->
            Resource.Success(entities.map { it.toDomain() })
        }
    }

    override fun getExpensesByDateRange(
        userId: String,
        startDate: Date,
        endDate: Date
    ): Flow<Resource<List<Expense>>> {
        return localDao.getExpensesByDateRange(userId, startDate.time, endDate.time).map { entities ->
            Resource.Success(entities.map { it.toDomain() })
        }
    }

    override suspend fun addExpense(expense: Expense): Resource<Unit> {
        return try {
            val result = remoteDataSource.addExpense(expense)
            if (result is Resource.Success) {
                localDao.insertExpense(ExpenseEntity.fromDomain(expense))
            }
            result
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add expense")
        }
    }

    override suspend fun updateExpense(expense: Expense): Resource<Unit> {
        return try {
            val result = remoteDataSource.updateExpense(expense)
            if (result is Resource.Success) {
                localDao.updateExpense(ExpenseEntity.fromDomain(expense))
            }
            result
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expense")
        }
    }

    override suspend fun deleteExpense(expenseId: String, userId: String): Resource<Unit> {
        return try {
            val result = remoteDataSource.deleteExpense(expenseId, userId)
            if (result is Resource.Success) {
                localDao.deleteExpenseById(expenseId)
            }
            result
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete expense")
        }
    }

    override suspend fun syncFromFirestore(userId: String) {
        try {
            val remoteExpenses = remoteDataSource.getAllExpensesOnce(userId)
            localDao.deleteAllForUser(userId)
            localDao.insertAll(remoteExpenses.map { ExpenseEntity.fromDomain(it) })
        } catch (e: Exception) {
            // Sync failed – local cache remains
        }
    }
}
