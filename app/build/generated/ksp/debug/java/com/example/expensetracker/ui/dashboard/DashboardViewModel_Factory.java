package com.example.expensetracker.ui.dashboard;

import com.example.expensetracker.domain.usecase.GetExpensesUseCase;
import com.example.expensetracker.domain.usecase.GetSummaryUseCase;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  private final Provider<GetSummaryUseCase> getSummaryUseCaseProvider;

  private final Provider<FirebaseAuth> authProvider;

  public DashboardViewModel_Factory(Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<GetSummaryUseCase> getSummaryUseCaseProvider, Provider<FirebaseAuth> authProvider) {
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
    this.getSummaryUseCaseProvider = getSummaryUseCaseProvider;
    this.authProvider = authProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(getExpensesUseCaseProvider.get(), getSummaryUseCaseProvider.get(), authProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<GetSummaryUseCase> getSummaryUseCaseProvider, Provider<FirebaseAuth> authProvider) {
    return new DashboardViewModel_Factory(getExpensesUseCaseProvider, getSummaryUseCaseProvider, authProvider);
  }

  public static DashboardViewModel newInstance(GetExpensesUseCase getExpensesUseCase,
      GetSummaryUseCase getSummaryUseCase, FirebaseAuth auth) {
    return new DashboardViewModel(getExpensesUseCase, getSummaryUseCase, auth);
  }
}
