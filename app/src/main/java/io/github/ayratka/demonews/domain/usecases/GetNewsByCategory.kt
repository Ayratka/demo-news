package io.github.ayratka.demonews.domain.usecases
import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.repository.CategoryRepository
import io.github.ayratka.demonews.domain.repository.NewsRepository
import javax.inject.Inject

class GetNewsByCategory @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(
        categoryId: Int,
        page: Int,
    ): List<News> = repository.getNewsByCategory(categoryId, page)
}