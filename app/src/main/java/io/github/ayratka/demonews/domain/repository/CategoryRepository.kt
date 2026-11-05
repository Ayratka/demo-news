package io.github.ayratka.demonews.domain.repository

import io.github.ayratka.demonews.domain.model.Category
import io.github.ayratka.demonews.domain.model.Outcome

interface CategoryRepository {
    suspend fun getCategories(): Outcome<List<Category>>
}