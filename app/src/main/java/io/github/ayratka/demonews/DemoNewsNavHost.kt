package io.github.ayratka.demonews

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.ayratka.demonews.presentation.categories.CategoriesScreen
import io.github.ayratka.demonews.presentation.navigation.Destinations
import io.github.ayratka.demonews.presentation.newsdetails.NewsDetailsScreen
import io.github.ayratka.demonews.presentation.newslist.NewsListScreen

@Composable
fun DemoNewsNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Destinations.CATEGORIES,
    ) {
        composable(Destinations.CATEGORIES) {
            CategoriesScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Destinations.newsList(categoryId))
                },
            )
        }
        composable(
            route = Destinations.NEWS_LIST,
            arguments = listOf(navArgument("categoryId") { type = NavType.LongType }),
        ) {
            NewsListScreen(
                onNewsClick = { newsId ->
                    navController.navigate(Destinations.newsDetails(newsId))
                },
            )
        }
        composable(
            route = Destinations.NEWS_DETAILS,
            arguments = listOf(navArgument("newsId") { type = NavType.LongType }),
        ) {
            NewsDetailsScreen()
        }
    }
}