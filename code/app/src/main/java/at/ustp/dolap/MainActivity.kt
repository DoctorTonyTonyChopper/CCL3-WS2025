package at.ustp.dolap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import at.ustp.dolap.data.local.DatabaseProvider
import at.ustp.dolap.data.repo.ClothingRepository
import at.ustp.dolap.data.repo.OutfitRepository
import at.ustp.dolap.ui.navigation.DolapNavGraph
import at.ustp.dolap.ui.theme.DolapTheme
import at.ustp.dolap.viewmodel.ClothingViewModel
import at.ustp.dolap.viewmodel.ClothingViewModelFactory
import at.ustp.dolap.viewmodel.OutfitViewModel
import at.ustp.dolap.viewmodel.OutfitViewModelFactory
import at.ustp.dolap.data.repo.TagRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DatabaseProvider.getDatabase(applicationContext)

        val clothingRepository = ClothingRepository(db.clothingDao())
        val tagRepository = TagRepository(db.tagDao())
        val clothingFactory = ClothingViewModelFactory(clothingRepository, tagRepository)

        val outfitRepository = OutfitRepository(db.outfitDao())
        val outfitFactory = OutfitViewModelFactory(outfitRepository)

        setContent {
            DolapTheme {
                val clothingVm: ClothingViewModel = viewModel(factory = clothingFactory)
                val outfitVm: OutfitViewModel = viewModel(factory = outfitFactory)

                DolapNavGraph(
                    clothingViewModel = clothingVm,
                    outfitViewModel = outfitVm
                )

                LaunchedEffect(Unit) {
                    clothingVm.ensurePredefinedTags()
                }
            }
        }
    }
}