package io.github.ayratka.demonews.domain.repository

import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.model.NewsDetails

interface NewsRepository {

    suspend fun getNewsByCategory(
        categoryId: Long,
        page: Int,
    ): List<News>

    suspend fun getNewsDetails(newsId: Long): NewsDetails

}