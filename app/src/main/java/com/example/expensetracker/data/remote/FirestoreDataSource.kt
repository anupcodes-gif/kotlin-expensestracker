package com.example.expensetracker.data.remote

import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.utils.Constants
import com.example.expensetracker.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private fun userExpensesCollection(userId: String) =
        firestore.collection(Constants.FIRESTORE_COLLECTION_USERS)
            .document(userId)
            .collection(Constants.FIRESTORE_COLLECTION_EXPENSES)

    fun getExpenses(userId: String): Flow<Resource<List<Expense>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = userExpensesCollection(userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown Firestore error"))
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Expense(
                            id = doc.id,
                            userId = userId,
                            title = doc.getString("title") ?: "",
                            amount = doc.getDouble("amount") ?: 0.0,
                            category = Category.valueOf(doc.getString("category") ?: Category.OTHER.name),
                            type = TransactionType.valueOf(doc.getString("type") ?: TransactionType.EXPENSE.name),
                            date = Date(doc.getLong("date") ?: System.currentTimeMillis()),
                            note = doc.getString("note") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(Resource.Success(expenses))
            }
        awaitClose { listener.remove() }
    }

    suspend fun addExpense(expense: Expense): Resource<Unit> {
        return try {
            val data = hashMapOf(
                "title" to expense.title,
                "amount" to expense.amount,
                "category" to expense.category.name,
                "type" to expense.type.name,
                "date" to expense.date.time,
                "note" to expense.note,
                "userId" to expense.userId
            )
            if (expense.id.isBlank()) {
                userExpensesCollection(expense.userId).add(data).await()
            } else {
                userExpensesCollection(expense.userId).document(expense.id).set(data).await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add expense")
        }
    }

    suspend fun updateExpense(expense: Expense): Resource<Unit> {
        return try {
            val data = mapOf(
                "title" to expense.title,
                "amount" to expense.amount,
                "category" to expense.category.name,
                "type" to expense.type.name,
                "date" to expense.date.time,
                "note" to expense.note
            )
            userExpensesCollection(expense.userId).document(expense.id).update(data).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update expense")
        }
    }

    suspend fun deleteExpense(expenseId: String, userId: String): Resource<Unit> {
        return try {
            userExpensesCollection(userId).document(expenseId).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete expense")
        }
    }

    suspend fun getAllExpensesOnce(userId: String): List<Expense> {
        return try {
            val snapshot = userExpensesCollection(userId).get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    Expense(
                        id = doc.id,
                        userId = userId,
                        title = doc.getString("title") ?: "",
                        amount = doc.getDouble("amount") ?: 0.0,
                        category = Category.valueOf(doc.getString("category") ?: Category.OTHER.name),
                        type = TransactionType.valueOf(doc.getString("type") ?: TransactionType.EXPENSE.name),
                        date = Date(doc.getLong("date") ?: System.currentTimeMillis()),
                        note = doc.getString("note") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
