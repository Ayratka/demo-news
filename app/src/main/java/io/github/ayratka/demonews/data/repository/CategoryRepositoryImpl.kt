package io.github.ayratka.demonews.data.repository

import io.github.ayratka.demonews.data.remote.api.NewsApi
import io.github.ayratka.demonews.data.remote.dto.requireSuccessList
import io.github.ayratka.demonews.data.remote.mappers.NewsMapper
import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.Category
import io.github.ayratka.demonews.domain.model.Outcome
import io.github.ayratka.demonews.domain.repository.CategoryRepository
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class CategoryRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val mapper: NewsMapper,
) : CategoryRepository {
    override suspend fun getCategories(): Outcome<List<Category>> {
        return try {
            val response = api.getCategories()
            requireSuccessList(response.code, response.list)
                .map { dtos -> dtos.map { mapper.mapToCategory(it) } }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            Outcome.Failure(AppError.Http(e.code(), e.message()))
        } catch (e: IOException) {
            Outcome.Failure(AppError.Network)
        } catch (e: Exception) {
            Outcome.Failure(AppError.Unknown)
        }
    }
}