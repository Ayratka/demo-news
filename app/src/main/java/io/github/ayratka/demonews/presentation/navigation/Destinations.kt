package io.github.ayratka.demonews.presentation.navigation

object Destinations {
    const val CATEGORIES = "categories"
    const val NEWS_LIST = "news_list/{categoryId}"
    const val NEWS_DETAILS = "news_details/{newsId}"
    fun newsList(categoryId: Long) = "news_list/$categoryId"

    fun newsDetails(newsId: Long) = "news_details/$newsId"
}