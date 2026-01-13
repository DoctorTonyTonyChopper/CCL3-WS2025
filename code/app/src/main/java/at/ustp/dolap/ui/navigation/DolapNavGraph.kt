package at.ustp.dolap.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import at.ustp.dolap.ui.components.DolapBottomBar
import at.ustp.dolap.ui.screens.detail.DetailScreen
import at.ustp.dolap.ui.screens.edit.AddEditScreen
import at.ustp.dolap.ui.screens.home.HomeScreen
import at.ustp.dolap.ui.screens.search.SearchScreen
import at.ustp.dolap.viewmodel.ClothingViewModel

@Composable
fun DolapNavGraph(
    viewModel: ClothingViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Show bottom bar only on main pages (Home & Search)
    val showBottomBar = currentRoute == Routes.HOME || currentRoute == Routes.SEARCH

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                DolapBottomBar(
                    currentRoute = currentRoute,
                    onAddClick = { navController.navigate(Routes.EDIT) },
                    onHomeClick = {
                        navController.navigate(Routes.HOME) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    },
                    onSearchClick = {
                        navController.navigate(Routes.SEARCH) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = viewModel,
                    onItemClick = { id -> navController.navigate("${Routes.DETAIL}/$id") },
                    onAddClick = { navController.navigate(Routes.EDIT) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) }
                )
            }

            composable(Routes.EDIT) {
                AddEditScreen(
                    viewModel = viewModel,
                    itemId = null,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EDIT_WITH_ID,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                AddEditScreen(
                    viewModel = viewModel,
                    itemId = id,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.SEARCH) {
                SearchScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onItemClick = { id -> navController.navigate("${Routes.DETAIL}/$id") }
                )
            }

            composable(
                route = "${Routes.DETAIL}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0

                DetailScreen(
                    viewModel = viewModel,
                    id = id,
                    onBack = { navController.popBackStack() },
                    onEdit = { editId -> navController.navigate("edit/$editId") }
                )
            }
        }
    }
}