package com.example.expensetracker.ui.dashboard

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.expensetracker.R
import kotlin.Int
import kotlin.String

public class DashboardFragmentDirections private constructor() {
  private data class ActionDashboardFragmentToAddExpenseFragment(
    public val expenseId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_dashboardFragment_to_addExpenseFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("expenseId", this.expenseId)
        return result
      }
  }

  public companion object {
    public fun actionDashboardFragmentToAddExpenseFragment(expenseId: String = ""): NavDirections =
        ActionDashboardFragmentToAddExpenseFragment(expenseId)
  }
}
