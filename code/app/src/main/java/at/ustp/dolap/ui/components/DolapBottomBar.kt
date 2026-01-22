package at.ustp.dolap.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import at.ustp.dolap.ui.navigation.Routes

@Composable
fun DolapBottomBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onOutfitsClick: () -> Unit,
    onInsightsClick: () -> Unit,
) {
    val homeSelected = currentRoute == Routes.HOME
    val outfitsSelected = currentRoute == Routes.OUTFITS
    val insightsSelected = currentRoute == Routes.INSIGHTS

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 3.dp
    ) {

        NavigationBarItem(
            selected = outfitsSelected,
            onClick = onOutfitsClick,
            icon = { Icon(Icons.Outlined.Style, contentDescription = "Outfits") },
            label = { Text("Outfits") },
            colors = navColors()
        )

        NavigationBarItem(
            selected = homeSelected,
            onClick = onHomeClick,
            icon = { Icon(Icons.Outlined.Checkroom, contentDescription = "Wardrobe") },
            label = { Text("Wardrobe") },
            colors = navColors()
        )

        NavigationBarItem(
            selected = insightsSelected,
            onClick = onInsightsClick,
            icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Insights") },
            label = { Text("Insights") },
            colors = navColors()
        )
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)