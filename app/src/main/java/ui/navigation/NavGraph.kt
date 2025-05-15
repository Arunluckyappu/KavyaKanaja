package com.kavyakanaja.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.*
import androidx.navigation.compose.*
import com.kavyakanaja.app.LanguageManager
import com.kavyakanaja.app.ui.screens.*
import com.kavyakanaja.app.viewmodel.*

sealed class Screen(val route: String) {
    object Login     : Screen("login")
    object Home      : Screen("home")
    object Profile   : Screen("profile")
    object Search    : Screen("search")
    object Bookmarks : Screen("bookmarks")
    object PoemDetail : Screen("poem/{poemId}") {
        fun createRoute(id: Int) = "poem/$id"
    }
    object Poet : Screen("poet/{poetId}") {
        fun createRoute(id: Int) = "poet/$id"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    poemViewModel: PoemViewModel,
    poetViewModel: PoetViewModel,
    authViewModel: AuthViewModel,
    languageManager: LanguageManager
) {
    val authState by authViewModel.state.collectAsState()
    val start = if (authState.isLoggedIn) Screen.Home.route else Screen.Login.route

    NavHost(navController = navController, startDestination = start) {
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, poemViewModel, languageManager)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, authViewModel, poemViewModel)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController, poemViewModel)
        }
        composable(Screen.Bookmarks.route) {
            BookmarkScreen(navController, poemViewModel)
        }
        composable(
            Screen.PoemDetail.route,
            arguments = listOf(navArgument("poemId") { type = NavType.IntType })
        ) { back ->
            PoemDetailScreen(
                back.arguments!!.getInt("poemId"),
                navController, poemViewModel, languageManager
            )
        }
        composable(
            Screen.Poet.route,
            arguments = listOf(navArgument("poetId") { type = NavType.IntType })
        ) { back ->
            PoetScreen(
                back.arguments!!.getInt("poetId"),
                navController, poemViewModel, poetViewModel
            )
        }
    }
}