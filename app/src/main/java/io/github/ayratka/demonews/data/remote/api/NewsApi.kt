package io.github.ayratka.demonews.data.remote.api

import io.github.ayratka.demonews.data.remote.dto.ApiResponse
import io.github.ayratka.demonews.data.remote.dto.CategoryDto
import io.github.ayratka.demonews.data.remote.dto.NewsDetailsDto
import io.github.ayratka.demonews.data.remote.dto.NewsDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApi {

    @GET("news/categories")
    suspend fun getCategories(): ApiResponse<CategoryDto>

    @GET("news")
    suspend fun getNewsByCategory(
        @Query("category_id") categoryId: Long,
        @Query("page") page: Int,
    ): List<NewsDto>

    @GET("news/{id}")
    suspend fun getNewsDetails(@Path("id") newsId: Long): NewsDetailsDto
}