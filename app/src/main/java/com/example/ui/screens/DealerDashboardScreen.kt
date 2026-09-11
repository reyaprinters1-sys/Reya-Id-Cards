package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.DealerEntity
import com.example.data.entity.OrderEntity
import com.example.model.CardType
import com.example.model.OrderStatus
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.example.ui.components.AddDealerDialog
import com.example.ui.components.DailyReportDialog
import com.example.ui.components.DealerManagementDialog
import com.example.ui.components.DealerProfileEditDialog
import com.example.ui.components.EditRateCardDialog
import com.example.ui.components.LanguageThemeQuickSwitchers
import com.example.ui.components.ReyaBrandGradient
import com.example.ui.components.ReyaLogoIcon
import com.example.ui.components.ReyaWhatsAppChatButton
import com.example.ui.components.WhatsAppShareDialog
import com.example.util.AppLanguage
import com.example.util.AppSettingsManager
import com.example.util.tr
import com.example.util.WhatsAppHelper
import com.example.viewmodel.CardProViewModel
import com.example.viewmodel.DashboardStats
import com.example.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealerDashboardScreen(
    viewModel: CardProViewModel,
    onNavigateToNewOrder: () -> Unit,
    onNavigateToTracking: (String?) -> Unit,
    onNavigateToRateCard: () -> Unit,
    onNavigateToFileManagement: () -> Unit
) {
    val context = LocalContext.current
    val session by viewModel.currentSession.collectAsState()
    val isAdmin = session?.role == UserRole.ADMIN

    val stats by viewModel.stats.collectAsState()
    val dealers by viewModel.allDealers.collectAsState()
    val selectedDealerCode by viewModel.selectedDealerCode.collectAsState()
    val orders by viewModel.filteredOrders.collectAsState()

    val currentDealer = if (isAdmin) {
        dealers.find { it.dealerCode == selectedDealerCode }
    } else {
        session?.dealer ?: dealers.find { it.dealerCode == session?.username }
    }

    val rateConfig by viewModel.rateCardConfig.collectAsState()
    var showDealerDropdown by remember { mutableStateOf(false) }
    var showAddDealerDialog by remember { mutableStateOf(false) }
    var showDealerManagementDialog by remember { mutableStateOf(false) }
    var showDealerProfileEditDialog by remember { mutableStateOf(false) }
    var showEditRateCardDialog by remember { mutableStateOf(false) }
    var showDailyReportDialog by remember { mutableStateOf(false) }
    var activeWhatsAppOrder by remember { mutableStateOf<OrderEntity?>(null) }

    val currentLang by AppSettingsManager.language.collectAsState()

    if (showDealerManagementDialog && isAdmin) {
        DealerManagementDialog(
            dealers = dealers,
            viewModel = viewModel,
            onDismiss = { showDealerManagementDialog = false },
            onAddNewDealer = {
                showDealerManagementDialog = false
                showAddDealerDialog = true
            }
        )
    }

    if (showDealerProfileEditDialog && currentDealer != null) {
        DealerProfileEditDialog(
            dealer = currentDealer,
            isAdminMode = isAdmin,
            viewModel = viewModel,
            onDismiss = { showDealerProfileEditDialog = false }
        )
    }

    if (showDailyReportDialog && isAdmin) {
        DailyReportDialog(
            viewModel = viewModel,
            onDismiss = { showDailyReportDialog = false }
        )
    }

    if (showEditRateCardDialog && isAdmin) {
        EditRateCardDialog(
            currentConfig = rateConfig,
            onDismiss = { showEditRateCardDialog = false },
            onSave = { updated ->
                viewModel.updateRateCard(updated)
                showEditRateCardDialog = false
                Toast.makeText(context, "விலைப்பட்டியல் புதுப்பிக்கப்பட்டது! ✓", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddDealerDialog) {
        AddDealerDialog(
            initialDealerCode = viewModel.generateNextDealerCode(),
            initialPin = viewModel.generateRandomPin(),
            onDismiss = { showAddDealerDialog = false },
            onSave = { code, name, shopType, phone, location, pin ->
                viewModel.addNewDealer(
                    code = code,
                    name = name,
                    shopType = shopType,
                    phone = phone,
                    location = location,
                    pin = pin,
                    onSuccess = { newDlr ->
                        showAddDealerDialog = false
                        Toast.makeText(context, "டீலர் #${newDlr.dealerCode} சேர்க்கப்பட்டது!", Toast.LENGTH_SHORT).show()
                    },
                    onError = { err ->
                        Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                    }
                )
            }
        )
    }

    if (activeWhatsAppOrder != null) {
        val o = activeWhatsAppOrder!!
        WhatsAppShareDialog(
            order = o,
            customerMessage = viewModel.generateWhatsAppMessage(o, forCustomer = true),
            dealerMessage = viewModel.generateWhatsAppMessage(o, forCustomer = false),
            onDismiss = { activeWhatsAppOrder = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Official Reya ID Cards Logo Icon replacing the previous icon/bag symbol
                        ReyaLogoIcon(size = 38.dp)

                        Column {
                            Text(
                                text = if (isAdmin) tr("Reya ஐடி கார்டு (அட்மின்)", "Reya ID Cards (Admin)", currentLang)
                                else (currentDealer?.name ?: tr("Reya ஐடி கார்டு டீலர்", "Reya Dealer", currentLang)),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.4.sp
                            )
                            Text(
                                text = if (isAdmin) tr("மத்திய உற்பத்தி கண்காணிப்பு", "Central Production Hub", currentLang)
                                else "${tr("டீலர் ஐடி", "Dealer ID", currentLang)}: #${currentDealer?.dealerCode ?: "DLR"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isAdmin) MaterialTheme.colorScheme.primary else Color(0xFF0284C7),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Quick Language and Theme switchers
                    LanguageThemeQuickSwitchers(compact = true)

                    // WhatsApp number 7708 910 190 with instant Chat option in TopBar
                    ReyaWhatsAppChatButton(compact = true)

                    if (isAdmin) {
                        // Admin: Daily Report (6 AM - 8 PM)
                        IconButton(
                            onClick = { showDailyReportDialog = true },
                            modifier = Modifier.testTag("btn_daily_report_top")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "தினசரி ரிப்போர்ட் (6 AM - 8 PM)",
                                tint = Color(0xFFF59E0B)
                            )
                        }

                        // Admin: Edit Rate Card Button
                        IconButton(
                            onClick = { showEditRateCardDialog = true },
                            modifier = Modifier.testTag("btn_edit_rate_card_top")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PriceChange,
                                contentDescription = "விலைப்பட்டியல் மாற்று",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Admin: Add Dealer Button with Generator
                        IconButton(
                            onClick = { showAddDealerDialog = true },
                            modifier = Modifier.testTag("btn_add_dealer_top")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "புதிய டீலர் சேர் & PIN",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Admin: Dealer Management & Password Directory
                        IconButton(
                            onClick = { showDealerManagementDialog = true },
                            modifier = Modifier.testTag("btn_dealer_management_top")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ManageAccounts,
                                contentDescription = "டீலர்கள் & கடவுச்சொல் டைரக்டரி",
                                tint = Color(0xFF6366F1)
                            )
                        }

                        // Admin: Dealer Selector Chip
                        Box {
                            FilterChip(
                                selected = true,
                                onClick = { showDealerDropdown = true },
                                label = {
                                    Text(
                                        text = if (selectedDealerCode == null) "அனைத்து டீலர்கள் (Hub)"
                                        else "${currentDealer?.name ?: "டீலர்"} (#${selectedDealerCode})"
                                    )
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                },
                                modifier = Modifier.testTag("dealer_selector_chip")
                            )

                            DropdownMenu(
                                expanded = showDealerDropdown,
                                onDismissRequest = { showDealerDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("அனைத்து டீலர்கள் (All Dealers / Central Hub)") },
                                    onClick = {
                                        viewModel.selectDealer(null)
                                        showDealerDropdown = false
                                    }
                                )
                                Divider()
                                dealers.forEach { dealer ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = "${dealer.name} (#${dealer.dealerCode})",
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${dealer.shopType} • ${dealer.location}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectDealer(dealer.dealerCode)
                                            showDealerDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Dealer: Edit Profile & Password in TopBar
                    if (!isAdmin && currentDealer != null) {
                        IconButton(
                            onClick = { showDealerProfileEditDialog = true },
                            modifier = Modifier.testTag("btn_dealer_edit_profile_top")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ManageAccounts,
                                contentDescription = "சுயவிவரம் & பாஸ்வேர்ட் மாற்று",
                                tint = Color(0xFF0284C7)
                            )
                        }
                    }

                    // Logout Button for both Admin and Dealer
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("btn_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "வெளியேறு (Logout)",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToNewOrder,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("+ புதிய ஆர்டர் (+ New Order)", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("new_order_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
        ) {
            // Hero Dealer Header Banner
            item {
                DealerHeroBanner(
                    dealer = currentDealer,
                    isAllView = selectedDealerCode == null,
                    isAdmin = isAdmin,
                    onEditProfile = if (currentDealer != null) { { showDealerProfileEditDialog = true } } else null
                )
            }

            // Dealer Profile Quick Settings Card
            if (!isAdmin && currentDealer != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showDealerProfileEditDialog = true }
                            .testTag("card_dealer_edit_profile"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                        border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF0284C7),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.ManageAccounts,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "சுயவிவரம் & கடவுச்சொல் மேலாண்மை",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0369A1)
                                    )
                                    Text(
                                        text = "📱 ${currentDealer.phone} • 📍 ${currentDealer.location}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF0C4A6E),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { showDealerProfileEditDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("மாற்று", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Financial & Production Key Stats
            item {
                Text(
                    text = "வணிக நிலவரம் & ஈட்டிய நிகர லாபம் (Earnings)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatsGrid(stats = stats)
            }

            // Quick Workflow Status Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorkflowQuickCard(
                        title = tr("பிரிண்டிங் ஆகிறது", "Printing", currentLang),
                        count = stats.printingCount,
                        icon = Icons.Default.Print,
                        containerColor = Color(0xFFEFF6FF),
                        accentColor = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTracking("PRINTING") }
                    )
                    WorkflowQuickCard(
                        title = tr("டெலிவரிக்கு அனுப்பப்பட்டது", "Dispatched", currentLang),
                        count = stats.dispatchedCount,
                        icon = Icons.Default.LocalShipping,
                        containerColor = Color(0xFFFEF3C7),
                        accentColor = Color(0xFFD97706),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTracking("DISPATCHED") }
                    )
                    WorkflowQuickCard(
                        title = tr("டெலிவரி முடிந்தது", "Delivered", currentLang),
                        count = stats.deliveredCount,
                        icon = Icons.Default.CheckCircle,
                        containerColor = Color(0xFFD1FAE5),
                        accentColor = Color(0xFF059669),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTracking("DELIVERED") }
                    )
                }
            }

            // Admin Daily Report Feature Card (6 AM - 8 PM)
            if (isAdmin) {
                item {
                    val allOrdersState by viewModel.allOrders.collectAsState()
                    val todayReport = remember(allOrdersState) {
                        viewModel.getDailyReport(strict6AmTo8Pm = true)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showDailyReportDialog = true }
                            .testTag("card_admin_daily_report"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                        border = BorderStroke(1.5.dp, ReyaBrandGradient)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF59E0B),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Assessment,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = "தினசரி ஆர்டர் & டெலிவரி ரிப்போர்ட்",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "⏰ காலை 06:00 AM - இரவு 08:00 PM",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF38BDF8),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${todayReport.totalOrdersCount} ஆர்டர்கள்",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399)
                                    )
                                }
                            }

                            Text(
                                text = "இன்று எந்த டீலர்க்கு எத்தனை கார்டுகள் டெலிவரி செய்ய வேண்டும் என்ற முழு விவரங்கள் மற்றும் நேரடி வாட்ஸ்அப் அறிக்கை வசதி.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showDailyReportDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("அறிக்கை பார்க்க", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val whatsappMsg = viewModel.generateDailyReportWhatsAppMessage(todayReport)
                                        WhatsAppHelper.shareReportViaWhatsApp(context, whatsappMsg)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("WhatsApp-ல் அனுப்பு", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Admin Dealer Directory & Password Management Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showDealerManagementDialog = true }
                            .testTag("card_admin_dealer_directory"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.5.dp, Color(0xFFC7D2FE))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF4F46E5),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.ManageAccounts,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = "டீலர்கள் & கடவுச்சொல் டைரக்டரி",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF1E1B4B)
                                        )
                                        Text(
                                            text = "${dealers.size} டீலர்கள் பதிவு • ஆட்டோ ID & PIN மேலாண்மை",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF4F46E5),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFFEEF2FF),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFC7D2FE))
                                ) {
                                    Text(
                                        text = "பார்க்க ➔",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4338CA)
                                    )
                                }
                            }

                            Text(
                                text = "டீலர்களின் யூசர்நேம், மொபைல், முகவரி மற்றும் பாஸ்வேர்ட் விவரங்களை நிர்வகியுங்கள். மறந்துவிட்ட டீலர்களுக்கு உடனடியாக PIN தெரிவிக்கலாம்.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF475569),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // WhatsApp Business Official Communication Card (7708 910 190)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = BorderStroke(1.dp, Color(0xFF86EFAC))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF25D366),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Reya ID Cards - WhatsApp உதவி மையம்",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF14532D)
                                )
                                Text(
                                    text = "அதிகாரப்பூர்வ WhatsApp நேரடி சேவை",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                                Text(
                                    text = if (isAdmin) "டீலர்களுக்கு கடவுச்சொல் & ஸ்டேட்டஸ் அப்டேட் அனுப்புங்கள்" else "நேரடி உதவி, டெலிவரி & ஆர்டர் விசாரணைகள்",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF15803D),
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Button(
                            onClick = {
                                WhatsAppHelper.contactOfficialAdmin(context, "வணக்கம் REYA ID CARDS! உதவி தேவைப்படுகிறது.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_dashboard_whatsapp_chat")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("சேட் செய்க", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Wholesale Rate Card Preview Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigateToRateCard() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PriceCheck,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "அதிகாரப்பூர்வ விலைப்பட்டியல் (Rate Card)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "PVC: ₹${"%.0f".format(rateConfig.onlyCardPvcDealer)} | RFID: ₹${"%.0f".format(rateConfig.onlyCardRfidDealer)} | பல்க் தள்ளுபடி",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isAdmin) {
                                TextButton(
                                    onClick = { showEditRateCardDialog = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("மாற்று", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // SOP Section 4 File Management Quick Access (Admin Only)
            if (isAdmin) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onNavigateToFileManagement() },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF1F5F9)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.FolderZip,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "கோப்பு மேலாண்மை & Google Drive (அட்மின் மட்டும்)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "ID_CARD_ORDERS_2026 போல்டர் மரம் & Details.txt SOP",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            } else {
                // Dealer Specific: 0 Machine Investment Highlight Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFD97706),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "₹0",
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "0 மெஷின் முதலீடு சிறப்பு நன்மை",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "பிரிண்டர், லேமினேஷன் மற்றும் மூலப்பொருள் எதுவும் வாங்க வேண்டாம்! நீங்கள் ஆர்டர்களை மட்டும் எடுத்து அனுப்புங்கள். நேரடி டெலிவரி & நிகர லாபம் உங்களுக்கே!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFB45309),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Recent Orders Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "சமீபத்திய ஆர்டர்கள் (Recent Orders)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigateToTracking(null) }) {
                        Text("அனைத்தும் பார் (${orders.size})")
                    }
                }
            }

            if (orders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "ஆர்டர்கள் எதுவும் இல்லை",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "புதிய ஆர்டரை பதிவு செய்ய '+ புதிய ஆர்டர்' பட்டனை அழுத்தவும்.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(orders.take(5)) { order ->
                    RecentOrderItem(
                        order = order,
                        onClick = { onNavigateToTracking(null) },
                        onWhatsAppClick = { activeWhatsAppOrder = order }
                    )
                }
            }
        }
    }
}

@Composable
private fun DealerHeroBanner(
    dealer: DealerEntity?,
    isAllView: Boolean,
    isAdmin: Boolean = false,
    onEditProfile: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        color = Color(0xFF0F172A)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF1E1B4B), // Deep Neon Violet
                            Color(0xFF4C1D95), // Royal Purple
                            Color(0xFF831843), // Hot Neon Rose
                            Color(0xFF0F172A)  // Dark Cyber Navy
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = ReyaBrandGradient,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isAllView) "CENTRAL HUB MODE" else "டீலர் ஐடி: #${dealer?.dealerCode ?: "DLR01"}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    if (dealer?.starterKitDelivered == true) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "✓ Starter Kit வழங்கப்பட்டது",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF34D399)
                            )
                        }
                    }
                }

                Text(
                    text = if (isAllView) "அனைத்து டீலர் நெட்வொர்க்" else (dealer?.name ?: "Reya Printers"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = if (isAllView) "உற்பத்தி மற்றும் விநியோக கண்காணிப்பு மையம்"
                    else "${dealer?.shopType} • 📍 ${dealer?.location} • 📱 ${dealer?.phone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )

                if (!isAllView && dealer != null && onEditProfile != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onEditProfile,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.18f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_edit_profile_banner")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("சுயவிவரம் & முகவரி திருத்து", fontSize = 11.sp, color = Color.White)
                        }

                        Button(
                            onClick = onEditProfile,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8).copy(alpha = 0.25f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_change_password_banner")
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🔑 PIN மாற்று", fontSize = 11.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsGrid(stats: DashboardStats) {
    val currentLang by AppSettingsManager.language.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = tr("ஈட்டிய நிகர லாபம்", "Net Profit Earned", currentLang),
                value = "₹${"%.0f".format(stats.totalDealerProfit)}",
                subtitle = tr("கார்டுக்கு ₹30 - ₹105 வரை", "₹30 - ₹105 per card", currentLang),
                containerColor = Color(0xFFECFDF5),
                textColor = Color(0xFF065F46),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = tr("மொத்த ஆர்டர்கள்", "Total Orders", currentLang),
                value = "${stats.totalOrders}",
                subtitle = "${stats.totalCards} ${tr("கார்டுகள் தயார்", "cards produced", currentLang)}",
                containerColor = Color(0xFFF0F9FF),
                textColor = Color(0xFF0369A1),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = tr("டீலர் கட்டிய தொகை", "Amount Paid", currentLang),
                value = "₹${"%.0f".format(stats.totalRevenue)}",
                subtitle = tr("செலுத்திய மொத்த வணிகம்", "Total Business Volume", currentLang),
                containerColor = Color(0xFFF8FAFC),
                textColor = Color(0xFF334155),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = tr("ஜீரோ மெஷின் முதலீடு", "Zero Machine Setup", currentLang),
                value = "₹0",
                subtitle = tr("உற்பத்தி முழுதும் எங்களது", "100% In-house production", currentLang),
                containerColor = Color(0xFFFFFBEB),
                textColor = Color(0xFFB45309),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = textColor.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = textColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun WorkflowQuickCard(
    title: String,
    count: Int,
    icon: ImageVector,
    containerColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RecentOrderItem(
    order: OrderEntity,
    onClick: () -> Unit,
    onWhatsAppClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = order.orderCode,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• ${order.quantity} கார்டுகள்",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = order.customerName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${if (order.cardType == "STANDARD_PVC") "Standard PVC" else "Smart RFID"} • லாபம்: ₹${"%.0f".format(order.totalDealerProfit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrderStatusBadge(status = order.status)

                if (onWhatsAppClick != null) {
                    IconButton(
                        onClick = onWhatsAppClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF25D366),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "WhatsApp அறிவிப்பு",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (bgColor, textColor, text) = when (status) {
        OrderStatus.PRINTING.code -> Triple(Color(0xFFEFF6FF), Color(0xFF2563EB), "பிரிண்டிங் ⚙️")
        OrderStatus.DISPATCHED.code -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "அனுப்பப்பட்டது 🚚")
        OrderStatus.DELIVERED.code -> Triple(Color(0xFFD1FAE5), Color(0xFF059669), "டெலிவரி ✓")
        else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "பெறப்பட்டது")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
