package com.example.expensetracker.data.repository;

import com.example.expensetracker.data.local.ExpenseDao;
import com.example.expensetracker.data.remote.FirestoreDataSource;
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
public final class ExpenseRepositoryImpl_Factory implements Factory<ExpenseRepositoryImpl> {
  private final Provider<ExpenseDao> localDaoProvider;

  private final Provider<FirestoreDataSource> remoteDataSourceProvider;

  public ExpenseRepositoryImpl_Factory(Provider<ExpenseDao> localDaoProvider,
      Provider<FirestoreDataSource> remoteDataSourceProvider) {
    this.localDaoProvider = localDaoProvider;
    this.remoteDataSourceProvider = remoteDataSourceProvider;
  }

  @Override
  public ExpenseRepositoryImpl get() {
    return newInstance(localDaoProvider.get(), remoteDataSourceProvider.get());
  }

  public static ExpenseRepositoryImpl_Factory create(Provider<ExpenseDao> localDaoProvider,
      Provider<FirestoreDataSource> remoteDataSourceProvider) {
    return new ExpenseRepositoryImpl_Factory(localDaoProvider, remoteDataSourceProvider);
  }

  public static ExpenseRepositoryImpl newInstance(ExpenseDao localDao,
      FirestoreDataSource remoteDataSource) {
    return new ExpenseRepositoryImpl(localDao, remoteDataSource);
  }
}
