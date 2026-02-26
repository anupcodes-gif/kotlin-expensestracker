package com.example.expensetracker.ui.addexpense;

import com.example.expensetracker.domain.usecase.AddExpenseUseCase;
import com.example.expensetracker.domain.usecase.DeleteExpenseUseCase;
import com.example.expensetracker.domain.usecase.GetExpensesUseCase;
import com.example.expensetracker.domain.usecase.UpdateExpenseUseCase;
import com.google.firebase.auth.FirebaseAuth;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class AddExpenseViewModel_Factory implements Factory<AddExpenseViewModel> {
  private final Provider<AddExpenseUseCase> addExpenseUseCaseProvider;

  private final Provider<UpdateExpenseUseCase> updateExpenseUseCaseProvider;

  private final Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider;

  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  private final Provider<FirebaseAuth> authProvider;

  public AddExpenseViewModel_Factory(Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<UpdateExpenseUseCase> updateExpenseUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider,
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<FirebaseAuth> authProvider) {
    this.addExpenseUseCaseProvider = addExpenseUseCaseProvider;
    this.updateExpenseUseCaseProvider = updateExpenseUseCaseProvider;
    this.deleteExpenseUseCaseProvider = deleteExpenseUseCaseProvider;
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
    this.authProvider = authProvider;
  }

  @Override
  public AddExpenseViewModel get() {
    return newInstance(addExpenseUseCaseProvider.get(), updateExpenseUseCaseProvider.get(), deleteExpenseUseCaseProvider.get(), getExpensesUseCaseProvider.get(), authProvider.get());
  }

  public static AddExpenseViewModel_Factory create(
      Provider<AddExpenseUseCase> addExpenseUseCaseProvider,
      Provider<UpdateExpenseUseCase> updateExpenseUseCaseProvider,
      Provider<DeleteExpenseUseCase> deleteExpenseUseCaseProvider,
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<FirebaseAuth> authProvider) {
    return new AddExpenseViewModel_Factory(addExpenseUseCaseProvider, updateExpenseUseCaseProvider, deleteExpenseUseCaseProvider, getExpensesUseCaseProvider, authProvider);
  }

  public static AddExpenseViewModel newInstance(AddExpenseUseCase addExpenseUseCase,
      UpdateExpenseUseCase updateExpenseUseCase, DeleteExpenseUseCase deleteExpenseUseCase,
      GetExpensesUseCase getExpensesUseCase, FirebaseAuth auth) {
    return new AddExpenseViewModel(addExpenseUseCase, updateExpenseUseCase, deleteExpenseUseCase, getExpensesUseCase, auth);
  }
}
