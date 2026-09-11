package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OrderEntity
import com.example.model.OrderStatus
import com.example.ui.components.DispatchDialog
import com.example.ui.components.LanguageThemeQuickSwitchers
import com.example.ui.components.PackingChecklistDialog
import com.example.ui.components.ReyaLogoIcon
import com.example.ui.components.ReyaWhatsAppChatButton
import com.example.ui.components.SopFileDetailsDialog
import com.example.ui.components.WhatsAppShareDialog
import com.example.util.AppSettingsManager
import com.example.util.tr
import com.example.viewmodel.CardProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    viewModel: CardProViewModel,
    initialStatusFilter: String? = null,
    onNavigateToNewOrder: () -> Unit
) {
    val currentLang by AppSettingsManager.language.collectAsState()
    val orders by viewModel.filteredOrders.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()

    // Dialog states
    var activePackingOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var activeDispatchOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var activeFileDetailsOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var activeWhatsAppOrder by remember { mutableStateOf<OrderEntity?>(null) }

    LaunchedEffect(initialStatusFilter) {
        if (initialStatusFilter != null) {
            viewModel.setStatusFilter(initialStatusFilter)
        }
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
                                text = tr("Reya ID Cards - ஆர்டர்கள்", "Reya ID Cards - Orders", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tr("உற்பத்தி மற்றும் விநியோக கண்காணிப்பு", "Production & Dispatch Tracking", currentLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    LanguageThemeQuickSwitchers(compact = true)

                    ReyaWhatsAppChatButton(compact = true)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewOrder,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Order")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("tracking_search_bar"),
                placeholder = { Text("ஆர்டர் எண், வாடிக்கையாளர் பெயர் தேடு...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Status Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { viewModel.setStatusFilter(null) },
                        label = { Text("அனைத்தும் (All)") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatus == OrderStatus.PRINTING.code,
                        onClick = { viewModel.setStatusFilter(OrderStatus.PRINTING.code) },
                        label = { Text("பிரிண்டிங் ஆகிறது ⚙️") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatus == OrderStatus.DISPATCHED.code,
                        onClick = { viewModel.setStatusFilter(OrderStatus.DISPATCHED.code) },
                        label = { Text("அனுப்பப்பட்டது 🚚") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatus == OrderStatus.DELIVERED.code,
                        onClick = { viewModel.setStatusFilter(OrderStatus.DELIVERED.code) },
                        label = { Text("டெலிவரி முடிந்தது ✓") }
                    )
                }
            }

            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "பொருந்தும் ஆர்டர்கள் எதுவும் இல்லை",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "தேடலை மாற்றவும் அல்லது புதிய ஆர்டரை பதிவு செய்யவும்.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        TrackingOrderCard(
                            order = order,
                            onOpenPackingChecklist = { activePackingOrder = order },
                            onOpenDispatch = { activeDispatchOrder = order },
                            onMarkDelivered = { viewModel.updateOrderStatus(order, OrderStatus.DELIVERED) },
                            onOpenWhatsApp = { activeWhatsAppOrder = order },
                            onOpenFileDetails = { activeFileDetailsOrder = order }
                        )
                    }
                }
            }
        }
    }

    // Packing Checklist Dialog
    activePackingOrder?.let { order ->
        PackingChecklistDialog(
            order = order,
            onDismiss = { activePackingOrder = null },
            onSave = { s1, s2, s3, s4 ->
                viewModel.updatePackingStages(order, s1, s2, s3, s4)
                activePackingOrder = null
            }
        )
    }

    // Dispatch Dialog
    activeDispatchOrder?.let { order ->
        DispatchDialog(
            order = order,
            onDismiss = { activeDispatchOrder = null },
            onDispatch = { courier, awb ->
                viewModel.dispatchOrder(order, courier, awb)
                activeDispatchOrder = null
            }
        )
    }

    // SOP File Details Dialog
    activeFileDetailsOrder?.let { order ->
        SopFileDetailsDialog(
            order = order,
            fileContent = viewModel.getOrderDetailsFileContent(order),
            onDismiss = { activeFileDetailsOrder = null }
        )
    }

    // WhatsApp Message Dialog
    activeWhatsAppOrder?.let { order ->
        WhatsAppShareDialog(
            order = order,
            customerMessage = viewModel.generateWhatsAppMessage(order, forCustomer = true),
            dealerMessage = viewModel.generateWhatsAppMessage(order, forCustomer = false),
            onDismiss = { activeWhatsAppOrder = null }
        )
    }
}

@Composable
private fun TrackingOrderCard(
    order: OrderEntity,
    onOpenPackingChecklist: () -> Unit,
    onOpenDispatch: () -> Unit,
    onMarkDelivered: () -> Unit,
    onOpenWhatsApp: () -> Unit,
    onOpenFileDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Order Code & Dealer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = order.orderCode,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${order.quantity} கார்டு",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "டீலர்: ${order.dealerName} (#${order.dealerCode})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OrderStatusBadge(status = order.status)
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Customer Info & Package Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.customerName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ph: ${order.customerPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${if (order.cardType == "STANDARD_PVC") "Standard PVC" else "Smart RFID"} • ${order.accessoriesPackage}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "டீலர் விலை: ₹${"%.0f".format(order.totalDealerPayable)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "லாபம்: +₹${"%.0f".format(order.totalDealerProfit)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                }
            }

            // Visual Progress Stepper (Received -> Printing -> Dispatched -> Delivered)
            OrderStepIndicator(status = order.status)

            // Courier / Logistics Info (if dispatched)
            if (order.status == OrderStatus.DISPATCHED.code || order.status == OrderStatus.DELIVERED.code) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "கொரியர்: ${order.courierName.ifBlank { "ST Courier" }} • AWB: ${order.awbNumber.ifBlank { "N/A" }}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            // 4-Stage Packing Summary Chip
            val checkedCount = listOf(order.stage1BatchPrinting, order.stage2IndividualPacking, order.stage3MasterZiplock, order.stage4ShippingPacking).count { it }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SOP பேக்கிங் நிலை: $checkedCount / 4 சரிபார்க்கப்பட்டது",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (checkedCount == 4) Color(0xFF059669) else Color(0xFFD97706),
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = onOpenPackingChecklist,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("பேக்கிங் SOP சரிபார்")
                }
            }

            // Primary Workflow Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Action 1: Dispatch or Mark Delivered
                if (order.status == OrderStatus.PRINTING.code || order.status == OrderStatus.RECEIVED.code) {
                    Button(
                        onClick = onOpenDispatch,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("அனுப்பு (Dispatch)")
                    }
                } else if (order.status == OrderStatus.DISPATCHED.code) {
                    Button(
                        onClick = onMarkDelivered,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("டெலிவரி முடிந்தது ✓")
                    }
                } else {
                    OutlinedButton(
                        onClick = onOpenDispatch,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AWB மாற்று")
                    }
                }

                // Action 2: WhatsApp Update
                FilledTonalButton(
                    onClick = onOpenWhatsApp,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFD1FAE5), contentColor = Color(0xFF065F46))
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp")
                }

                // Action 3: File details (.txt)
                IconButton(onClick = onOpenFileDetails) {
                    Icon(Icons.Default.Article, contentDescription = "File Details", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun OrderStepIndicator(status: String) {
    val currentStep = when (status) {
        OrderStatus.PRINTING.code -> 1
        OrderStatus.DISPATCHED.code -> 2
        OrderStatus.DELIVERED.code -> 3
        else -> 0
    }

    val steps = listOf("ஆர்டர்", "பிரிண்டிங்", "அனுப்பப்பட்டது", "டெலிவரி")

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, stepName ->
            val isCompleted = index <= currentStep
            val isCurrent = index == currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = if (isCompleted) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1),
                    shape = CircleShape,
                    modifier = Modifier.size(if (isCurrent) 22.dp else 16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stepName,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCompleted) MaterialTheme.colorScheme.primary else Color(0xFF64748B)
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(2.dp)
                        .background(if (index < currentStep) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1))
                )
            }
        }
    }
}
