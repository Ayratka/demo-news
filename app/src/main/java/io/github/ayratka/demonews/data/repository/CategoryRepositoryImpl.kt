package io.github.ayratka.demonews.data.repository

import io.github.ayratka.demonews.data.remote.api.NewsApi
import io.github.ayratka.demonews.data.remote.dto.requireSuccess
import io.github.ayratka.demonews.data.remote.dto.requireSuccessList
import io.github.ayratka.demonews.data.remote.mappers.NewsMapper
import io.github.ayratka.demonews.domain.model.Category
import io.github.ayratka.demonews.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val mapper: NewsMapper,
    ): CategoryRepository {
    override suspend fun getCategories(): List<Category> {
        val response = api.getCategories()
        val dto = requireSuccessList(
            response.code,
            list = response.list,
        )
        return dto.map { mapper.mapToCategory(it) }
    }
}