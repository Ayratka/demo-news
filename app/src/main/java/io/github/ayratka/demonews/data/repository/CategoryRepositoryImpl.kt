package io.github.ayratka.demonews.data.repository

import io.github.ayratka.demonews.data.remote.api.NewsApi
import io.github.ayratka.demonews.data.remote.mappers.NewsMapper
import io.github.ayratka.demonews.domain.model.Category
import io.github.ayratka.demonews.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val mapper: NewsMapper,
    ): CategoryRepository {
    override suspend fun getCategories(): List<Category> {
        return api.getCategories().map { mapper.mapToCategory(it) }
    }
}