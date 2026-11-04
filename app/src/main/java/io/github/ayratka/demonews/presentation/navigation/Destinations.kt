package io.github.ayratka.demonews.presentation.navigation

object Destinations {
    const val CATEGORIES = "categories"
    const val NEWS_LIST = "news_list/{categoryId}"
    fun newsList(categoryId: Long) = "news_list/$categoryId"
}