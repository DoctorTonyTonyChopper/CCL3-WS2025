package at.ustp.dolap.ui.screens.insights

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import at.ustp.dolap.data.local.ClothingWearStats
import at.ustp.dolap.data.local.OutfitWearStats
import at.ustp.dolap.viewmodel.InsightsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel,
    onOpenOutfit: (Int) -> Unit,
    onOpenClothing: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val dateFmt = DateTimeFormatter.ofPattern("MMM d, yyyy")

    fun epochDayToLabel(epochDay: Long?): String {
        if (epochDay == null) return "Never"
        return LocalDate.ofEpochDay(epochDay).format(dateFmt)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Insights", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "Based on your wear log",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // --- KPI / summary card ---
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Overview", style = MaterialTheme.typography.titleMedium)

                    if (state.isEmptyWearLog) {
                        Text(
                            "No wear history yet. Start logging outfits from an Outfit Detail screen (Wear Today or date picker) to see insights.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricChip(label = "Total wears", value = state.totalWearEntries.toString())
                            MetricChip(label = "Worn today", value = state.outfitsWornToday.toString())
                            MetricChip(label = "Outfits (30d)", value = state.distinctOutfitsLast30Days.toString())
                        }
                    }
                }
            }
        }

        // --- Most worn outfits ---
        item {
            SectionCard(
                title = "Most worn outfits",
                subtitle = if (state.isEmptyWearLog) "Log wears to populate this list" else null
            ) {
                if (state.mostWornOutfits.isEmpty() || state.isEmptyWearLog) {
                    EmptyInline("No data yet.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.mostWornOutfits.forEach { row ->
                            OutfitRow(
                                row = row,
                                lastWornLabel = epochDayToLabel(row.lastWorn),
                                onClick = { onOpenOutfit(row.outfitId) }
                            )
                        }
                    }
                }
            }
        }

        // --- Never worn clothes ---
        item {
            SectionCard(
                title = "Never worn clothes",
                subtitle = "Clothes that haven't appeared in any worn outfit"
            ) {
                if (state.neverWornClothes.isEmpty()) {
                    EmptyInline("Nice — everything has been worn at least once (or you have no clothes yet).")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.neverWornClothes.forEach { row ->
                            ClothingRow(
                                row = row,
                                lastWornLabel = "Never",
                                onClick = { onOpenClothing(row.clothingId) }
                            )
                        }
                    }
                }
            }
        }

        // --- Least worn clothes ---
        item {
            SectionCard(
                title = "Least worn clothes",
                subtitle = "Lowest wear counts across your closet"
            ) {
                if (state.leastWornClothes.isEmpty() || state.isEmptyWearLog) {
                    EmptyInline(if (state.isEmptyWearLog) "No wear data yet." else "No clothes found.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.leastWornClothes.forEach { row ->
                            ClothingRow(
                                row = row,
                                lastWornLabel = epochDayToLabel(row.lastWorn),
                                onClick = { onOpenClothing(row.clothingId) }
                            )
                        }
                    }
                }
            }
        }

        // --- Not worn in 90 days ---
        item {
            SectionCard(
                title = "Not worn in 90 days",
                subtitle = "Older than 90 days since last wear (includes never worn)"
            ) {
                if (state.notWornIn90Days.isEmpty()) {
                    EmptyInline("Everything has been worn recently (within 90 days), or no wear data yet.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.notWornIn90Days.forEach { row ->
                            ClothingRow(
                                row = row,
                                lastWornLabel = epochDayToLabel(row.lastWorn),
                                onClick = { onOpenClothing(row.clothingId) }
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun MetricChip(label: String, value: String) {
    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            content()
        }
    }
}

@Composable
private fun EmptyInline(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun OutfitRow(
    row: OutfitWearStats,
    lastWornLabel: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = {
            Text(row.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text("Last worn: $lastWornLabel")
        },
        trailingContent = {
            Text("${row.wearCount}×", style = MaterialTheme.typography.titleMedium)
        }
    )
    Divider()
}

@Composable
private fun ClothingRow(
    row: ClothingWearStats,
    lastWornLabel: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = {
            Text(row.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            val cat = row.category?.takeIf { it.isNotBlank() }
            Text(
                buildString {
                    if (cat != null) append(cat).append(" • ")
                    append("Last worn: ").append(lastWornLabel)
                }
            )
        },
        trailingContent = {
            Text("${row.wearCount}×", style = MaterialTheme.typography.titleMedium)
        }
    )
    Divider()
}