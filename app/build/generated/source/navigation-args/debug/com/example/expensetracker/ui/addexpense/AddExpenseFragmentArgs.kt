package com.example.expensetracker.ui.addexpense

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class AddExpenseFragmentArgs(
  public val expenseId: String = "",
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("expenseId", this.expenseId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("expenseId", this.expenseId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): AddExpenseFragmentArgs {
      bundle.setClassLoader(AddExpenseFragmentArgs::class.java.classLoader)
      val __expenseId : String?
      if (bundle.containsKey("expenseId")) {
        __expenseId = bundle.getString("expenseId")
        if (__expenseId == null) {
          throw IllegalArgumentException("Argument \"expenseId\" is marked as non-null but was passed a null value.")
        }
      } else {
        __expenseId = ""
      }
      return AddExpenseFragmentArgs(__expenseId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): AddExpenseFragmentArgs {
      val __expenseId : String?
      if (savedStateHandle.contains("expenseId")) {
        __expenseId = savedStateHandle["expenseId"]
        if (__expenseId == null) {
          throw IllegalArgumentException("Argument \"expenseId\" is marked as non-null but was passed a null value")
        }
      } else {
        __expenseId = ""
      }
      return AddExpenseFragmentArgs(__expenseId)
    }
  }
}
