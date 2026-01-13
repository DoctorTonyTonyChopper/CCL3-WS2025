package at.ustp.dolap.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Checkroom
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
    onSearchClick: () -> Unit
) {
    val homeSelected = currentRoute == Routes.HOME
    val searchSelected = currentRoute == Routes.SEARCH

    NavigationBar(
        containerColor = BlackNavBar,   // always black
        contentColor = Color.White
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onAddClick,
            icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White,
                indicatorColor = DarkGrayIndicator
            )
        )

        NavigationBarItem(
            selected = homeSelected,
            onClick = onHomeClick,
            icon = { Icon(Icons.Outlined.Checkroom, contentDescription = "Home") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White,
                indicatorColor = DarkGrayIndicator
            )
        )

        NavigationBarItem(
            selected = searchSelected,
            onClick = onSearchClick,
            icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White,
                indicatorColor = DarkGrayIndicator
            )
        )
    }
}