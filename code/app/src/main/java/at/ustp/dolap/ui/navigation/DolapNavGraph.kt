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
import at.ustp.dolap.ui.screens.outfits.OutfitAddEditScreen
import at.ustp.dolap.ui.screens.outfits.OutfitDetailScreen
import at.ustp.dolap.ui.screens.outfits.OutfitListScreen
import at.ustp.dolap.ui.screens.outfits.OutfitPickClothesScreen
import at.ustp.dolap.ui.screens.search.SearchScreen
import at.ustp.dolap.ui.screens.splash.SplashScreen
import at.ustp.dolap.viewmodel.ClothingViewModel
import at.ustp.dolap.viewmodel.OutfitViewModel
import at.ustp.dolap.viewmodel.InsightsViewModel
import at.ustp.dolap.ui.screens.insights.InsightsScreen

@Composable
fun DolapNavGraph(
    clothingViewModel: ClothingViewModel,
    outfitViewModel: OutfitViewModel,
    insightsViewModel: InsightsViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Show bottom bar only on main tabs
    val showBottomBar =
        currentRoute == Routes.HOME ||
                currentRoute == Routes.OUTFITS ||
                currentRoute == Routes.INSIGHTS

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                DolapBottomBar(
                    currentRoute = currentRoute,
                    onHomeClick = {
                        navController.navigate(Routes.HOME) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    },
                    onOutfitsClick = {
                        navController.navigate(Routes.OUTFITS) {
                            launchSingleTop = true
                        }
                    },
                    onInsightsClick = {
                        navController.navigate(Routes.INSIGHTS) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(padding)
        ) {

            composable(Routes.SPLASH) {
                SplashScreen(navController = navController)
            }

            // ---- Wardrobe ----
            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = clothingViewModel,
                    onItemClick = { id -> navController.navigate("${Routes.DETAIL}/$id") },
                    onAddClick = { navController.navigate(Routes.EDIT) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) }
                )
            }

            composable(Routes.SEARCH) {
                SearchScreen(
                    viewModel = clothingViewModel,
                    onBack = { navController.popBackStack() },
                    onItemClick = { id -> navController.navigate("${Routes.DETAIL}/$id") }
                )
            }

            composable(Routes.EDIT) {
                AddEditScreen(
                    viewModel = clothingViewModel,
                    itemId = null,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EDIT_WITH_ID,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStack ->
                val id = backStack.arguments?.getInt("id")
                AddEditScreen(
                    viewModel = clothingViewModel,
                    itemId = id,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "${Routes.DETAIL}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStack ->
                val id = backStack.arguments?.getInt("id") ?: 0
                DetailScreen(
                    viewModel = clothingViewModel,
                    id = id,
                    onBack = { navController.popBackStack() },
                    onEdit = { editId -> navController.navigate("edit/$editId") }
                )
            }

            // ---- Outfits ----
            composable(Routes.OUTFITS) {
                OutfitListScreen(
                    viewModel = outfitViewModel,
                    onOpenOutfit = { id -> navController.navigate("${Routes.OUTFIT_DETAIL}/$id") },
                    onAddOutfit = { navController.navigate(Routes.OUTFIT_EDIT) }
                )
            }

            // Create Outfit
            composable(Routes.OUTFIT_EDIT) {
                OutfitAddEditScreen(
                    viewModel = outfitViewModel,
                    clothingViewModel = clothingViewModel,
                    outfitId = null,
                    onBack = { navController.popBackStack() }
                )
            }

            // Edit Outfit
            composable(
                route = Routes.OUTFIT_EDIT_WITH_ID,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStack ->
                val id = backStack.arguments?.getInt("id")
                OutfitAddEditScreen(
                    viewModel = outfitViewModel,
                    clothingViewModel = clothingViewModel,
                    outfitId = id,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "${Routes.OUTFIT_DETAIL}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStack ->
                val id = backStack.arguments?.getInt("id") ?: 0
                OutfitDetailScreen(
                    viewModel = outfitViewModel,
                    outfitId = id,
                    onBack = { navController.popBackStack() },
                    onEdit = { editId -> navController.navigate("outfit_edit/$editId") },
                    onPickClothes = { oid -> navController.navigate("outfit_pick_clothes/$oid") }
                )
            }

            composable(
                route = Routes.OUTFIT_PICK_CLOTHES_WITH_ID,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStack ->
                val id = backStack.arguments?.getInt("id") ?: 0
                OutfitPickClothesScreen(
                    outfitId = id,
                    outfitViewModel = outfitViewModel,
                    clothingViewModel = clothingViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Insights
            composable(Routes.INSIGHTS) {
                InsightsScreen(
                    viewModel = insightsViewModel,
                    onOpenOutfit = { id -> navController.navigate("${Routes.OUTFIT_DETAIL}/$id") },
                    onOpenClothing = { id -> navController.navigate("${Routes.DETAIL}/$id") }
                )
            }
        }
    }
}