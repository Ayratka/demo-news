package io.github.ayratka.demonews

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.ayratka.demonews.presentation.categories.CategoriesScreen
import io.github.ayratka.demonews.presentation.navigation.Destinations

@Composable
fun DemoNewsNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.CATEGORIES,   // <- первый экран
    ) {
        composable(Destinations.CATEGORIES) {
            CategoriesScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Destinations.newsList(categoryId))
                },
            )
        }
    }
}