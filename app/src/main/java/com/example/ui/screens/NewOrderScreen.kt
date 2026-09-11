package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccessoriesPackage
import com.example.model.CardType
import com.example.ui.components.LanguageThemeQuickSwitchers
import com.example.ui.components.ReyaLogoIcon
import com.example.ui.components.ReyaWhatsAppChatButton
import com.example.util.AppSettingsManager
import com.example.util.tr
import com.example.viewmodel.CardProViewModel
import com.example.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    viewModel: CardProViewModel,
    onOrderSubmitted: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentLang by AppSettingsManager.language.collectAsState()
    val session by viewModel.currentSession.collectAsState()

    val customerName by viewModel.formCustomerName.collectAsState()
    val customerPhone by viewModel.formCustomerPhone.collectAsState()
    val cardType by viewModel.formCardType.collectAsState()
    val accessoriesPackage by viewModel.formPackage.collectAsState()
    val quantity by viewModel.formQuantity.collectAsState()
    val notes by viewModel.formNotes.collectAsState()
    val photoUploaded by viewModel.formPhotoSelected.collectAsState()
    val paymentUploaded by viewModel.formPaymentProofSelected.collectAsState()

    val pricing by viewModel.livePricing.collectAsState()
    val selectedDealerCode by viewModel.selectedDealerCode.collectAsState()
    val dealers by viewModel.allDealers.collectAsState()
    val currentDealer = if (session?.role == UserRole.DEALER) {
        session?.dealer ?: dealers.find { it.dealerCode == session?.username }
    } else {
        dealers.find { it.dealerCode == selectedDealerCode } ?: dealers.firstOrNull()
    }

    var isSubmitting by remember { mutableStateOf(false) }

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
                                text = tr("Reya ID Cards - புதிய ஆர்டர்", "Reya ID Cards - New Order", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${tr("டீலர்", "Dealer", currentLang)}: ${currentDealer?.name ?: "Reya Printers"} (#${currentDealer?.dealerCode ?: "DLR01"})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    LanguageThemeQuickSwitchers(compact = true)

                    ReyaWhatsAppChatButton(compact = true)
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            // Section 1: Customer Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "வாடிக்கையாளர் விவரம் (Customer Info)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { viewModel.formCustomerName.value = it },
                            label = { Text("வாடிக்கையாளர் / மாணவர் / ஊழியர் பெயர் *") },
                            placeholder = { Text("எ.கா: கார்த்திக் ராஜா (Karthik Raja)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("customer_name_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { viewModel.formCustomerPhone.value = it },
                            label = { Text("மொபைல் எண் * (WhatsApp / Mobile)") },
                            placeholder = { Text("எ.கா: 9843210987") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("customer_phone_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                    }
                }
            }

            // Section 2: Card Type & Accessories Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "கார்டு & Accessories சேர்க்கை",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Card Type Selector (PVC vs RFID)
                        Text(
                            text = "கார்டு வகை தேர்வு:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CardTypeTab(
                                title = "ஸ்டாண்டர்ட் PVC",
                                subtitle = "பள்ளி, கல்லூரி, அலுவலகம்",
                                selected = cardType == CardType.STANDARD_PVC,
                                onClick = { viewModel.setFormCardType(CardType.STANDARD_PVC) },
                                modifier = Modifier.weight(1f)
                            )
                            CardTypeTab(
                                title = "ஸ்மார்ட் / RFID",
                                subtitle = "அட்டெண்டன்ஸ் & அக்சஸ்",
                                selected = cardType == CardType.SMART_RFID,
                                onClick = { viewModel.setFormCardType(CardType.SMART_RFID) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Accessories Package Selector
                        Text(
                            text = "பேக்கேஜ் சேர்க்கை (Accessories):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AccessoriesPackage.values().forEach { pkg ->
                                val unitPrice = pkg.getUnitDealerPrice(cardType)
                                val unitMrp = pkg.getUnitMrp(cardType)
                                val unitProfit = pkg.getUnitDealerProfit(cardType)

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setFormPackage(pkg) }
                                        .border(
                                            width = if (accessoriesPackage == pkg) 2.dp else 1.dp,
                                            color = if (accessoriesPackage == pkg) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    color = if (accessoriesPackage == pkg) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                    else MaterialTheme.colorScheme.surface
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            RadioButton(
                                                selected = accessoriesPackage == pkg,
                                                onClick = { viewModel.setFormPackage(pkg) }
                                            )
                                            Column {
                                                Text(
                                                    text = pkg.titleTa,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (accessoriesPackage == pkg) FontWeight.Bold else FontWeight.Medium
                                                )
                                                Text(
                                                    text = "MRP: ₹${"%.0f".format(unitMrp)} | டீலர் லாபம்: ₹${"%.0f".format(unitProfit)}/கார்டு",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF059669),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "₹${"%.0f".format(unitPrice)}",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Quantity Stepper
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "கார்டுகள் எண்ணிக்கை (Quantity):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1, 10, 25, 50, 100).forEach { preset ->
                                    FilterChip(
                                        selected = quantity == preset,
                                        onClick = { viewModel.setFormQuantity(preset) },
                                        label = { Text("$preset") }
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilledIconButton(
                                onClick = { viewModel.setFormQuantity(quantity - 1) },
                                enabled = quantity > 1
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }

                            Text(
                                text = "$quantity கார்டுகள்",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )

                            FilledIconButton(
                                onClick = { viewModel.setFormQuantity(quantity + 1) }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }

            // Section 3: File Uploads (Photo & UPI Payment Proof) from SOP Section 3.2
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "கோப்பு & ரசீது பதிவேற்றம் (SOP Mandate)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Upload 1: ID Card Photo
                        UploadItem(
                            number = 1,
                            title = "ID Card டிசைன் அல்லது வாடிக்கையாளர் புகைப்படம்",
                            subtitle = if (photoUploaded) "✓ புகைப்படம் இணைக்கப்பட்டுள்ளது (Ready)" else "போட்டோ அல்லது டிசைன் PDF/JPG அப்லோட் செய்",
                            isUploaded = photoUploaded,
                            onClick = {
                                viewModel.formPhotoSelected.value = !photoUploaded
                                Toast.makeText(context, if (!photoUploaded) "புகைப்படம் இணைக்கப்பட்டது" else "நீக்கப்பட்டது", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // Upload 2: UPI / GPay Receipt
                        UploadItem(
                            number = 2,
                            title = "UPI / GPay ரசீது ஸ்கிரீன்ஷாட்",
                            subtitle = if (paymentUploaded) "✓ பணம் செலுத்திய ரசீது சரிபார்க்கப்பட்டது" else "GPay / PhonePe செலுத்திய ரசீது ஸ்கிரீன்ஷாட்",
                            isUploaded = paymentUploaded,
                            onClick = {
                                viewModel.formPaymentProofSelected.value = !paymentUploaded
                                Toast.makeText(context, if (!paymentUploaded) "ரசீது இணைக்கப்பட்டது" else "நீக்கப்பட்டது", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // UPI ID Info card
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column {
                                    Text(
                                        text = "உற்பத்தி ஹப் UPI ID: reyaidcards@okhdfcbank",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "பணம் செலுத்தியதும் ரசீதை உடனே அப்லோட் செய்யவும்.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { viewModel.formNotes.value = it },
                            label = { Text("கூடுதல் குறிப்புகள் (Notes / School Name)") },
                            placeholder = { Text("எ.கா: 10ம் வகுப்பு மாணவர்கள், சிவப்பு லேன்யார்டு") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // Section 4: Live Payment & Profit Formula Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(16.dp)
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
                            Text(
                                text = "செலுத்த வேண்டிய தொகை (Auto-Formula)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = pricing.discountSlabDescription,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFBBF24)
                                )
                            }
                        }

                        Divider(color = Color(0xFF334155))

                        PriceRow(label = "அடிப்படை டீலர் விலை (Unit Base):", value = "₹${"%.2f".format(pricing.unitBaseDealerPrice)} / கார்டு", isDark = true)
                        if (pricing.discountPerCard > 0) {
                            PriceRow(label = "பல்க் தள்ளுபடி சலுகை:", value = "- ₹${"%.2f".format(pricing.discountPerCard)} / கார்டு", isDark = true, highlightColor = Color(0xFF34D399))
                        }
                        PriceRow(label = "இறுதி டீலர் விலை (${pricing.quantity} கார்டுகள்):", value = "₹${"%.2f".format(pricing.effectiveUnitDealerPrice)} x ${pricing.quantity}", isDark = true)

                        Divider(color = Color(0xFF334155))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "செலுத்த வேண்டிய மொத்தத் தொகை:",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "₹${"%.2f".format(pricing.totalDealerPayable)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "டீலர் நிகர லாபம் (Customer MRP ₹${"%.0f".format(pricing.totalCustomerMrp)}):",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )
                            Text(
                                text = "+ ₹${"%.2f".format(pricing.totalDealerProfit)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            // Section 5: Submit Button
            item {
                Button(
                    onClick = {
                        isSubmitting = true
                        viewModel.submitNewOrder(
                            onSuccess = { created ->
                                isSubmitting = false
                                Toast.makeText(context, "ஆர்டர் வெற்றிகரமாக பதிவு செய்யப்பட்டது! #${created.orderCode}", Toast.LENGTH_LONG).show()
                                onOrderSubmitted()
                            },
                            onError = { err ->
                                isSubmitting = false
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_order_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Text(
                                text = "ஆர்டரை சமர்ப்பி (Submit Order)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardTypeTab(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(10.dp)
            ),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun UploadItem(
    number: Int,
    title: String,
    subtitle: String,
    isUploaded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = if (isUploaded) Color(0xFF10B981) else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(10.dp)
            ),
        color = if (isUploaded) Color(0xFFECFDF5) else Color(0xFFF8FAFC)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = if (isUploaded) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$number",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isUploaded) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                contentDescription = null,
                tint = if (isUploaded) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    isDark: Boolean = false,
    highlightColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isDark) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = highlightColor ?: (if (isDark) Color.White else MaterialTheme.colorScheme.onSurface)
        )
    }
}
