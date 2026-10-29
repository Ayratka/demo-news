package io.github.ayratka.demonews.data.remote.api

import io.github.ayratka.demonews.data.remote.dto.CategoryDto
import io.github.ayratka.demonews.data.remote.dto.NewsDetailsDto
import io.github.ayratka.demonews.data.remote.dto.NewsDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApi {

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("news")
    suspend fun getNewsByCategory(
        @Query("category_id") categoryId: Int,
        @Query("page") page: Int,
    ): List<NewsDto>

    @GET("news/{id}")
    suspend fun getNewsDetails(@Path("id") newsId: Int): NewsDetailsDto
}