package at.ustp.dolap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.ustp.dolap.data.local.ClothingWearStats
import at.ustp.dolap.data.local.OutfitWearStats
import at.ustp.dolap.data.repo.InsightsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class InsightsViewModel(
    private val repo: InsightsRepository
) : ViewModel() {

    private val todayEpochDay: Long = LocalDate.now().toEpochDay()
    private val ninetyDaysAgo: Long = todayEpochDay - 90L
    private val thirtyDaysAgo: Long = todayEpochDay - 30L

    private val topOutfitsLimit = 5
    private val clothesLimit = 8

    val uiState: StateFlow<InsightsUiState> =
        combine(
            repo.totalWearEntries(),
            repo.outfitsWornToday(todayEpochDay),
            repo.distinctOutfitsWornInRange(thirtyDaysAgo, todayEpochDay)
        ) { totalWear: Int, wornToday: Int, distinct30d: Int ->
            Triple(totalWear, wornToday, distinct30d)
        }.combine(
            repo.mostWornOutfits(topOutfitsLimit)
        ) { triple: Triple<Int, Int, Int>, mostWorn: List<OutfitWearStats> ->
            Pair(triple, mostWorn)
        }.combine(
            repo.neverWornClothes(clothesLimit)
        ) { pair: Pair<Triple<Int, Int, Int>, List<OutfitWearStats>>, neverWorn: List<ClothingWearStats> ->
            Pair(pair, neverWorn)
        }.combine(
            repo.leastWornClothes(clothesLimit)
        ) { pair2: Pair<Pair<Triple<Int, Int, Int>, List<OutfitWearStats>>, List<ClothingWearStats>>,
            leastWorn: List<ClothingWearStats> ->
            Pair(pair2, leastWorn)
        }.combine(
            repo.clothesNotWornSince(ninetyDaysAgo, clothesLimit)
        ) { pair3: Pair<Pair<Pair<Triple<Int, Int, Int>, List<OutfitWearStats>>, List<ClothingWearStats>>, List<ClothingWearStats>>,
            notWorn90d: List<ClothingWearStats> ->

            val triple = pair3.first.first.first
            val mostWorn = pair3.first.first.second
            val neverWorn = pair3.first.second
            val leastWorn = pair3.second

            val totalWear = triple.first
            val wornToday = triple.second
            val distinct30d = triple.third

            InsightsUiState(
                isEmptyWearLog = totalWear == 0,
                totalWearEntries = totalWear,
                outfitsWornToday = wornToday,
                distinctOutfitsLast30Days = distinct30d,
                mostWornOutfits = mostWorn,
                neverWornClothes = neverWorn,
                leastWornClothes = leastWorn,
                notWornIn90Days = notWorn90d
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InsightsUiState()
        )
}