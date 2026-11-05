package io.github.ayratka.demonews.data.repository

import io.github.ayratka.demonews.data.remote.api.NewsApi
import io.github.ayratka.demonews.data.remote.dto.requireSuccessList
import io.github.ayratka.demonews.data.remote.mappers.NewsMapper
import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.model.NewsDetails
import io.github.ayratka.demonews.domain.model.Outcome
import io.github.ayratka.demonews.domain.repository.NewsRepository
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val mapper: NewsMapper,
) : NewsRepository {

    override suspend fun getNewsByCategory(
        categoryId: Long,
        page: Int,
    ): Outcome<List<News>> {
        return try {
            val response = api.getNewsByCategory(categoryId, page)
            requireSuccessList(response.code, response.list)
                .map { dtos -> dtos.map { mapper.mapToDomain(it) } }
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

    override suspend fun getNewsDetails(newsId: Long): Outcome<NewsDetails> {
        return try {
            val response = api.getNewsDetails(newsId)
            Outcome.Success(mapper.mapDetailsToDomain(response))
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