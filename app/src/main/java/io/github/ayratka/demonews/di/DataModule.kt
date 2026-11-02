package io.github.ayratka.demonews.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.ayratka.demonews.data.remote.repository.CategoryRepositoryImpl
import io.github.ayratka.demonews.data.remote.repository.NewsRepositoryImpl
import io.github.ayratka.demonews.domain.repository.CategoryRepository
import io.github.ayratka.demonews.domain.repository.NewsRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    @Binds
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

}