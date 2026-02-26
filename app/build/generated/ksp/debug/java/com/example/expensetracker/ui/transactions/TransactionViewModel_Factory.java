package com.example.expensetracker.ui.transactions;

import com.example.expensetracker.domain.usecase.GetExpensesUseCase;
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
public final class TransactionViewModel_Factory implements Factory<TransactionViewModel> {
  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  private final Provider<FirebaseAuth> authProvider;

  public TransactionViewModel_Factory(Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<FirebaseAuth> authProvider) {
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
    this.authProvider = authProvider;
  }

  @Override
  public TransactionViewModel get() {
    return newInstance(getExpensesUseCaseProvider.get(), authProvider.get());
  }

  public static TransactionViewModel_Factory create(
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<FirebaseAuth> authProvider) {
    return new TransactionViewModel_Factory(getExpensesUseCaseProvider, authProvider);
  }

  public static TransactionViewModel newInstance(GetExpensesUseCase getExpensesUseCase,
      FirebaseAuth auth) {
    return new TransactionViewModel(getExpensesUseCase, auth);
  }
}
