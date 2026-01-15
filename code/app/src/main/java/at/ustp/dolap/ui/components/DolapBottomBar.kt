package at.ustp.dolap.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import at.ustp.dolap.ui.navigation.Routes
import at.ustp.dolap.ui.theme.BlackNavBar
import at.ustp.dolap.ui.theme.DarkGrayIndicator

@Composable
fun DolapBottomBar(
    currentRoute: String?,
    onAddClick: () -> Unit,
    onHomeClick: () -> Unit,
    onOutfitsClick: () -> Unit,
    onInsightsClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val homeSelected = currentRoute == Routes.HOME
    val outfitsSelected = currentRoute == Routes.OUTFITS
    val insightsSelected = currentRoute == Routes.INSIGHTS
    val searchSelected = currentRoute == Routes.SEARCH

    NavigationBar(
        containerColor = BlackNavBar,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            selected = homeSelected,
            onClick = onHomeClick,
            icon = { Icon(Icons.Outlined.Checkroom, contentDescription = "Wardrobe") },
            alwaysShowLabel = false,
            colors = navColors()
        )

        NavigationBarItem(
            selected = outfitsSelected,
            onClick = onOutfitsClick,
            icon = { Icon(Icons.Outlined.Style, contentDescription = "Outfits") },
            alwaysShowLabel = false,
            colors = navColors()
        )

        NavigationBarItem(
            selected = insightsSelected,
            onClick = onInsightsClick,
            icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Insights") },
            alwaysShowLabel = false,
            colors = navColors()
        )
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color.White,
    unselectedIconColor = Color.White,
    indicatorColor = DarkGrayIndicator
)