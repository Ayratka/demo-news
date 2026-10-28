package io.github.ayratka.demonews.domain.repository

import io.github.ayratka.demonews.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
}