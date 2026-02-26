package com.example.expensetracker.ui.statistics;

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
public final class StatisticsViewModel_Factory implements Factory<StatisticsViewModel> {
  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  private final Provider<FirebaseAuth> authProvider;

  public StatisticsViewModel_Factory(Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<FirebaseAuth> authProvider) {
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
    this.authProvider = authProvider;
  }

  @Override
  public StatisticsViewModel get() {
    return newInstance(getExpensesUseCaseProvider.get(), authProvider.get());
  }

  public static StatisticsViewModel_Factory create(
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider,
      Provider<FirebaseAuth> authProvider) {
    return new StatisticsViewModel_Factory(getExpensesUseCaseProvider, authProvider);
  }

  public static StatisticsViewModel newInstance(GetExpensesUseCase getExpensesUseCase,
      FirebaseAuth auth) {
    return new StatisticsViewModel(getExpensesUseCase, auth);
  }
}
