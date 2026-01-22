package at.ustp.dolap.ui.screens.insights

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            item {
                Text(
                    "Based on your wear log",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            //  Overview
            item {
                OverviewCard(
                    isEmptyWearLog = state.isEmptyWearLog,
                    totalWear = state.totalWearEntries,
                    wornToday = state.outfitsWornToday,
                    distinct30d = state.distinctOutfitsLast30Days
                )
            }

            //  Most worn outfits
            item {
                SectionCard(
                    title = "Most worn outfits",
                    subtitle = if (state.isEmptyWearLog) "Log wears from Outfit Detail to populate this" else null
                ) {
                    if (state.isEmptyWearLog || state.mostWornOutfits.isEmpty()) {
                        EmptyInline("No data yet.")
                    } else {
                        RankedOutfitList(
                            rows = state.mostWornOutfits.take(5),
                            epochDayToLabel = ::epochDayToLabel,
                            onOpenOutfit = onOpenOutfit
                        )
                    }
                }
            }

            //  Never worn clothes
            item {
                SectionCard(
                    title = "Never worn clothes",
                    subtitle = "Clothes that haven't appeared in any worn outfit"
                ) {
                    if (state.neverWornClothes.isEmpty()) {
                        EmptyInline("Nice — everything has been worn at least once (or you have no clothes yet).")
                    } else {
                        RankedClothingList(
                            rows = state.neverWornClothes,
                            lastWornOverride = "Never",
                            epochDayToLabel = ::epochDayToLabel,
                            onOpenClothing = onOpenClothing
                        )
                    }
                }
            }

            //  Least worn clothes
            item {
                SectionCard(
                    title = "Least worn clothes",
                    subtitle = "Lowest wear counts across your closet"
                ) {
                    if (state.isEmptyWearLog || state.leastWornClothes.isEmpty()) {
                        EmptyInline(if (state.isEmptyWearLog) "No wear data yet." else "No clothes found.")
                    } else {
                        RankedClothingList(
                            rows = state.leastWornClothes.take(5),
                            lastWornOverride = null,
                            epochDayToLabel = ::epochDayToLabel,
                            onOpenClothing = onOpenClothing
                        )
                    }
                }
            }

            // Not worn in 90 days
            item {
                SectionCard(
                    title = "Not worn in 90 days",
                    subtitle = "Older than 90 days since last wear (includes never worn)"
                ) {
                    if (state.notWornIn90Days.isEmpty()) {
                        EmptyInline("Everything has been worn recently (within 90 days), or no wear data yet.")
                    } else {
                        RankedClothingList(
                            rows = state.notWornIn90Days,
                            lastWornOverride = null,
                            epochDayToLabel = ::epochDayToLabel,
                            onOpenClothing = onOpenClothing
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(
    isEmptyWearLog: Boolean,
    totalWear: Int,
    wornToday: Int,
    distinct30d: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isEmptyWearLog)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.BarChart,
                    contentDescription = null,
                    tint = if (isEmptyWearLog)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Overview",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isEmptyWearLog)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }

            if (isEmptyWearLog) {
                Text(
                    "No wear history yet. Start logging outfits from an Outfit Detail screen (Worn today or Pick date) to see insights.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(label = "Total wears", value = totalWear.toString(), modifier = Modifier.weight(1f))
                    MetricCard(label = "Worn today", value = wornToday.toString(), modifier = Modifier.weight(1f))
                    MetricCard(label = "Outfits (30d)", value = distinct30d.toString(), modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun EmptyInline(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun RankedOutfitList(
    rows: List<OutfitWearStats>,
    epochDayToLabel: (Long?) -> String,
    onOpenOutfit: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        rows.forEachIndexed { idx, row ->
            val last = epochDayToLabel(row.lastWorn)
            RankedRow(
                rank = idx + 1,
                title = row.name,
                subtitle = "Last worn: $last",
                trailing = "${row.wearCount}×",
                onClick = { onOpenOutfit(row.outfitId) }
            )
            if (idx != rows.lastIndex) {
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
private fun RankedClothingList(
    rows: List<ClothingWearStats>,
    lastWornOverride: String?,
    epochDayToLabel: (Long?) -> String,
    onOpenClothing: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        rows.forEachIndexed { idx, row ->
            val last = lastWornOverride ?: epochDayToLabel(row.lastWorn)
            val cat = row.category?.takeIf { it.isNotBlank() }
            val subtitle = buildString {
                if (cat != null) append(cat).append(" • ")
                append("Last worn: ").append(last)
            }

            RankedRow(
                rank = idx + 1,
                title = row.name,
                subtitle = subtitle,
                trailing = "${row.wearCount}×",
                onClick = { onOpenClothing(row.clothingId) }
            )
            if (idx != rows.lastIndex) {
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
private fun RankedRow(
    rank: Int,
    title: String,
    subtitle: String,
    trailing: String,
    onClick: () -> Unit
) {
    val (chipBg, chipFg) = when (rank) {
        1 -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        2 -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        3 -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        leadingContent = {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = chipBg,
                contentColor = chipFg,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        },
        headlineContent = {
            Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(subtitle, maxLines = 2, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(trailing, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}