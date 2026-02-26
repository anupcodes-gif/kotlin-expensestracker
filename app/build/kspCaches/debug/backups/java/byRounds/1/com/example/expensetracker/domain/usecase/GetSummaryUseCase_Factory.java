package com.example.expensetracker.domain.usecase;

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
public final class GetSummaryUseCase_Factory implements Factory<GetSummaryUseCase> {
  private final Provider<GetExpensesUseCase> getExpensesUseCaseProvider;

  public GetSummaryUseCase_Factory(Provider<GetExpensesUseCase> getExpensesUseCaseProvider) {
    this.getExpensesUseCaseProvider = getExpensesUseCaseProvider;
  }

  @Override
  public GetSummaryUseCase get() {
    return newInstance(getExpensesUseCaseProvider.get());
  }

  public static GetSummaryUseCase_Factory create(
      Provider<GetExpensesUseCase> getExpensesUseCaseProvider) {
    return new GetSummaryUseCase_Factory(getExpensesUseCaseProvider);
  }

  public static GetSummaryUseCase newInstance(GetExpensesUseCase getExpensesUseCase) {
    return new GetSummaryUseCase(getExpensesUseCase);
  }
}
