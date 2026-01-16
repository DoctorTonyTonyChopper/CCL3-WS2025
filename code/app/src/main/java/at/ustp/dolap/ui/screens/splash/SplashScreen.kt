package at.ustp.dolap.ui.screens.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true } // Splash aus Backstack entfernen
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Dolap",
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
