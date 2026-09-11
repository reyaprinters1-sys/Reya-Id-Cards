package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccessoriesPackage
import com.example.model.CardType
import com.example.model.PricingCalculator
import com.example.model.RateCardConfig
import com.example.ui.components.EditRateCardDialog
import com.example.ui.components.LanguageThemeQuickSwitchers
import com.example.ui.components.ReyaLogoIcon
import com.example.ui.components.ReyaWhatsAppChatButton
import com.example.util.AppSettingsManager
import com.example.util.tr
import com.example.viewmodel.CardProViewModel
import com.example.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateCardSopScreen(
    viewModel: CardProViewModel? = null,
    onNavigateToNewOrder: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Standard PVC, 1: Smart RFID, 2: Bulk Calculator & Starter Kit
    var showEditDialog by remember { mutableStateOf(false) }

    val currentLang by AppSettingsManager.language.collectAsState()
    val rateConfig by (viewModel?.rateCardConfig?.collectAsState() ?: remember { mutableStateOf(RateCardConfig()) })
    val session by (viewModel?.currentSession?.collectAsState() ?: remember { mutableStateOf(null) })
    val isAdmin = session?.role == UserRole.ADMIN

    if (showEditDialog && viewModel != null) {
        EditRateCardDialog(
            currentConfig = rateConfig,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.updateRateCard(updated)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReyaLogoIcon(size = 34.dp)
                        Column {
                            Text(
                                text = tr("Reya ID Cards - விலைப்பட்டியல்", "Reya Rate Card", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tr("ஹோல்சேல் & டீலர் லாப வரம்பு அட்டவணை", "Wholesale & Dealer Margin Chart", currentLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    LanguageThemeQuickSwitchers(compact = true)

                    ReyaWhatsAppChatButton(compact = true)

                    if (isAdmin) {
                        Spacer(modifier = Modifier.width(4.dp))
                        FilledTonalButton(
                            onClick = { showEditDialog = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("btn_open_edit_rate_card")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(tr("விலை மாற்று", "Edit Rates", currentLang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToNewOrder,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("ஆர்டர் செய்") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("A. ஸ்டாண்டர்ட் PVC") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("B. ஸ்மார்ட் RFID") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("C. கால்குலேட்டர் & கிட்") }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        item {
                            RateCardTable(
                                title = "A. ஸ்டாண்டர்ட் PVC ஐடி கார்டுகள்",
                                subtitle = "பள்ளி, கல்லூரி மாணவர்கள் & அரசுப் பணி விண்ணப்பதாரர்கள்",
                                cardType = CardType.STANDARD_PVC,
                                config = rateConfig
                            )
                        }
                        item { BulkSlabsOverview(config = rateConfig) }
                    }
                    1 -> {
                        item {
                            RateCardTable(
                                title = "B. ஸ்மார்ட் / RFID கார்டுகள்",
                                subtitle = "பள்ளி பயோமெட்ரிக் அட்டெண்டன்ஸ் & தொழிற்சாலை அக்சஸ் கன்ட்ரோல்",
                                cardType = CardType.SMART_RFID,
                                config = rateConfig
                            )
                        }
                        item { BulkSlabsOverview(config = rateConfig) }
                    }
                    2 -> {
                        item { InteractiveQuotationCalculator(config = rateConfig) }
                        item { StarterKitChecklist() }
                    }
                }
            }
        }
    }
}

@Composable
private fun RateCardTable(
    title: String,
    subtitle: String,
    cardType: CardType,
    config: RateCardConfig = RateCardConfig()
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider()

            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "பேக்கேஜ் விவரம்",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1.8f)
                )
                Text(
                    text = "டீலர் விலை",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "விற்பனை MRP",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.weight(1.1f)
                )
                Text(
                    text = "டீலர் லாபம்",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    modifier = Modifier.weight(1f)
                )
            }

            AccessoriesPackage.values().forEachIndexed { index, pkg ->
                val dealerRate = config.getUnitDealerPrice(pkg, cardType)
                val mrp = config.getUnitMrp(pkg, cardType)
                val profit = config.getUnitDealerProfit(pkg, cardType)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (index % 2 == 0) Color(0xFFF8FAFC) else Color.White)
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.8f)) {
                        Text(
                            text = pkg.titleTa,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = pkg.subtitleTa,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = "₹${"%.0f".format(dealerRate)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "₹${"%.0f".format(mrp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        modifier = Modifier.weight(1.1f)
                    )
                    Text(
                        text = "+₹${"%.0f".format(profit)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BulkSlabsOverview(config: RateCardConfig = RateCardConfig()) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFD97706))
                Text(
                    text = "C. மொத்த ஆர்டர் தள்ளுபடி சலுகை (Bulk Order Slabs)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
            }

            Text(
                text = "• 1 – 10 கார்டுகள்: வழக்கமான டீலர் விலை (Standard Wholesale Rate)",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF78350F)
            )
            Text(
                text = "• 11 – 50 கார்டுகள்: சிறிய பள்ளிகள்/நிறுவனங்களுக்கு கார்டுக்கு ₹${"%.0f".format(config.discount11to50)} தள்ளுபடி",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF78350F)
            )
            Text(
                text = "• 51 – 200 கார்டுகள்: பெரிய கல்லூரிகள்/தொழிற்சாலைகளுக்கு கார்டுக்கு ₹${"%.0f".format(config.discount51to200)} தள்ளுபடி",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF78350F)
            )
            Text(
                text = "• 200+ கார்டுகள்: வருடாந்திர பள்ளி திட்டங்களுக்கு ₹${"%.0f".format(config.discount200Plus)} மெகா தள்ளுபடி",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF78350F),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InteractiveQuotationCalculator(config: RateCardConfig = RateCardConfig()) {
    var calcCardType by remember { mutableStateOf(CardType.STANDARD_PVC) }
    var calcPackage by remember { mutableStateOf(AccessoriesPackage.CARD_CASE_MULTICOLOR_LANYARD) }
    var calcQty by remember { mutableStateOf(50) }

    val calcResult = remember(calcCardType, calcPackage, calcQty, config) {
        PricingCalculator.calculate(calcCardType, calcPackage, calcQty, config)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "பல்க் கொட்டேஷன் கால்குலேட்டர் (Live Quotation)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Select Card Type
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = calcCardType == CardType.STANDARD_PVC,
                    onClick = { calcCardType = CardType.STANDARD_PVC },
                    label = { Text("PVC கார்டு") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = calcCardType == CardType.SMART_RFID,
                    onClick = { calcCardType = CardType.SMART_RFID },
                    label = { Text("RFID ஸ்மார்ட் கார்டு") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Select Package
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AccessoriesPackage.values().forEach { pkg ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { calcPackage = pkg },
                        color = if (calcPackage == pkg) MaterialTheme.colorScheme.primaryContainer else Color(0xFFF1F5F9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = calcPackage == pkg, onClick = { calcPackage = pkg })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = pkg.titleTa,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (calcPackage == pkg) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Quantity presets
            Text(text = "எண்ணிக்கை: $calcQty கார்டுகள்", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(10, 25, 50, 100, 250).forEach { qty ->
                    FilterChip(
                        selected = calcQty == qty,
                        onClick = { calcQty = qty },
                        label = { Text("$qty") }
                    )
                }
            }

            // Result summary
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "கொட்டேஷன் சுருக்கம் (${calcResult.discountSlabDescription}):",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "டீலர் கொடுக்க வேண்டியது:", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "₹${"%.0f".format(calcResult.totalDealerPayable)}", color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "டீலர் ஈட்டும் லாபம்:", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodySmall)
                        Text(text = "+₹${"%.0f".format(calcResult.totalDealerProfit)}", color = Color(0xFF10B981), fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun StarterKitChecklist() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "டீலர் ஸ்டார்ட்டர் கிட் (Starter Kit SOP Sec 6)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            val items = listOf(
                "PVC மற்றும் RFID மாதிரி கார்டுகளின் தொகுப்பு (Card Samples)",
                "மல்டிகலர் பிராண்டட் லேன்யார்டு சாம்பிள்கள் (Lanyard Samples)",
                "அக்ரிலிக் Table-Top Display Standee ('இங்கு அனைத்து ஐடி கார்டுகளும் செய்து தரப்படும்')",
                "வடிவமைத்த முழுமையான A4 வண்ண பிரவுச்சர் (A4 Color Brochure)"
            )

            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                    Text(text = item, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
