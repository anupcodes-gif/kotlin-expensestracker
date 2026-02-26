package com.example.expensetracker.ui.transactions

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.expensetracker.R
import kotlin.Int
import kotlin.String

public class TransactionListFragmentDirections private constructor() {
  private data class ActionTransactionsFragmentToAddExpenseFragment(
    public val expenseId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_transactionsFragmentToAddExpenseFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("expenseId", this.expenseId)
        return result
      }
  }

  public companion object {
    public fun actionTransactionsFragmentToAddExpenseFragment(expenseId: String = ""): NavDirections
        = ActionTransactionsFragmentToAddExpenseFragment(expenseId)
  }
}
