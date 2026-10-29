package io.github.ayratka.demonews.data.remote.repository

import io.github.ayratka.demonews.data.remote.api.NewsApi
import io.github.ayratka.demonews.data.remote.mappers.NewsMapper
import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.model.NewsDetails
import io.github.ayratka.demonews.domain.repository.NewsRepository
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val mapper: NewsMapper,
) : NewsRepository {

    override suspend fun getNewsByCategory(
        categoryId: Int,
        page: Int,
    ): List<News> {
        val dtos = api.getNewsByCategory(categoryId, page)
        return dtos.map { mapper.mapToDomain(it) }
    }

    override suspend fun getNewsDetails(newsId: Int): NewsDetails {
        val dto = api.getNewsDetails(newsId)
        return mapper.mapDetailsToDomain(dto)
    }
}