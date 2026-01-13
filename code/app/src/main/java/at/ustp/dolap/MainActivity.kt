package at.ustp.dolap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import at.ustp.dolap.data.local.DatabaseProvider
import at.ustp.dolap.data.repo.ClothingRepository
import at.ustp.dolap.ui.theme.DolapTheme
import at.ustp.dolap.viewmodel.ClothingViewModel
import at.ustp.dolap.viewmodel.ClothingViewModelFactory
import at.ustp.dolap.ui.navigation.DolapNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = ClothingRepository(db.clothingDao())
        val factory = ClothingViewModelFactory(repository)

        setContent {
            DolapTheme {
                // Creating the ViewModel using the factory
                val vm: ClothingViewModel = viewModel(factory = factory)

                // Show the real app screen (Home)
                DolapNavGraph(viewModel = vm)            }
        }
    }
}