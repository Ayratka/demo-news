package io.github.ayratka.demonews.domain.usecases

import io.github.ayratka.demonews.domain.model.NewsDetails
import io.github.ayratka.demonews.domain.repository.NewsRepository
import javax.inject.Inject

class GetNewsDetails @Inject constructor(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(newsId: Int): NewsDetails =
        repository.getNewsDetails(newsId)
}