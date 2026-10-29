package io.github.ayratka.demonews.domain.usecases

import io.github.ayratka.demonews.domain.model.Category
import io.github.ayratka.demonews.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategories @Inject constructor(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(): List<Category> = repository.getCategories()
}