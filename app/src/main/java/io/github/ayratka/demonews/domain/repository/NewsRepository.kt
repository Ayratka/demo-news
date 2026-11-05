package io.github.ayratka.demonews.domain.repository

import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.model.NewsDetails
import io.github.ayratka.demonews.domain.model.Outcome

interface NewsRepository {

    suspend fun getNewsByCategory(
        categoryId: Long,
        page: Int,
    ): Outcome<List<News>>

    suspend fun getNewsDetails(newsId: Long): Outcome<NewsDetails>

}