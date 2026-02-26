package com.example.expensetracker.ui.auth

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.expensetracker.R

public class LoginFragmentDirections private constructor() {
  public companion object {
    public fun actionLoginFragmentToDashboardFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_dashboardFragment)

    public fun actionLoginFragmentToRegisterFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_registerFragment)
  }
}
