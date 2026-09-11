package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.DealerEntity
import com.example.util.WhatsAppHelper
import com.example.viewmodel.CardProViewModel

/**
 * Admin Panel - Dealer Management and Password Directory.
 * Displays all dealers, their auto-generated usernames/IDs, mobile numbers, locations,
 * and current passwords/PINs. Allows viewing, editing, resetting passwords, and sharing via WhatsApp.
 */
@Composable
fun DealerManagementDialog(
    dealers: List<DealerEntity>,
    viewModel: CardProViewModel,
    onDismiss: () -> Unit,
    onAddNewDealer: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedDealerForEdit by remember { mutableStateOf<DealerEntity?>(null) }
    val revealedPins = remember { mutableStateMapOf<String, Boolean>() }

    val filteredDealers = remember(dealers, searchQuery) {
        if (searchQuery.isBlank()) {
            dealers
        } else {
            val q = searchQuery.trim().lowercase()
            dealers.filter {
                it.dealerCode.lowercase().contains(q) ||
                it.name.lowercase().contains(q) ||
                it.phone.contains(q) ||
                it.location.lowercase().contains(q) ||
                it.shopType.lowercase().contains(q)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 12.dp)
                .testTag("dialog_dealer_management"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
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
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF4F46E5),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ManageAccounts,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "டீலர்கள் & கடவுச்சொல் டைரக்டரி",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "டீலர் யூசர்நேம், PIN & முகவரி மேலாண்மை",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4F46E5),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "மூடு")
                    }
                }

                // Info banner for forgotten passwords
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "டீலர்கள் தங்கள் கடவுச்சொல்லை மறந்துவிட்டால், இந்த டைரக்டரியில் அவர்களின் PIN-ஐப் பார்த்து அவர்களுக்கு WhatsApp-ல் தெரிவிக்கலாம் அல்லது புதிய PIN அமைக்கலாம்.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF92400E),
                            fontSize = 11.sp
                        )
                    }
                }

                // Search & Add Dealer Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("டீலர் பெயர் / ஐடி / எண் தேடுக...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("input_search_dealers")
                    )

                    Button(
                        onClick = onAddNewDealer,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("btn_add_dealer_from_management"),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ புதிய டீலர்", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Dealers Count Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "மொத்த டீலர்கள்: ${filteredDealers.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Dealers List
                if (filteredDealers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "டீலர்கள் யாரும் காணப்படவில்லை!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredDealers, key = { it.dealerCode }) { dealer ->
                            val isRevealed = revealedPins[dealer.dealerCode] ?: false

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Row 1: Code, Name, Shop Type
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                color = Color(0xFF3B82F6),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "#${dealer.dealerCode}",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Text(
                                                text = dealer.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text(
                                            text = dealer.shopType,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF64748B),
                                            fontSize = 11.sp
                                        )
                                    }

                                    // Row 2: Phone & Address
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Phone,
                                                contentDescription = null,
                                                tint = Color(0xFF059669),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = dealer.phone,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = Color(0xFFDC2626),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = dealer.location,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                    }

                                    Divider(color = Color(0xFFE2E8F0))

                                    // Row 3: Credentials & Actions
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Password / PIN display with eye & copy
                                        Surface(
                                            color = Color(0xFFEFF6FF),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Key,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2563EB),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = "PIN: ",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF1E40AF),
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = if (isRevealed) dealer.pin else "••••",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = Color(0xFF1E3A8A)
                                                )
                                                IconButton(
                                                    onClick = {
                                                        revealedPins[dealer.dealerCode] = !isRevealed
                                                    },
                                                    modifier = Modifier.size(22.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = "காட்டு/மறை",
                                                        tint = Color(0xFF3B82F6),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        val clip = ClipData.newPlainText("Dealer PIN", dealer.pin)
                                                        clipboard.setPrimaryClip(clip)
                                                        Toast.makeText(context, "PIN காப்பி செய்யப்பட்டது: ${dealer.pin}", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(22.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "காப்பி",
                                                        tint = Color(0xFF64748B),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Action buttons: Edit Profile/PIN & WhatsApp
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            // Edit Button
                                            OutlinedButton(
                                                onClick = {
                                                    selectedDealerForEdit = dealer
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(32.dp),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("திருத்து", fontSize = 11.sp)
                                            }

                                            // WhatsApp Credentials Button
                                            Button(
                                                onClick = {
                                                    val msg = WhatsAppHelper.buildDealerCredentialsMessage(
                                                        dealerName = dealer.name,
                                                        dealerCode = dealer.dealerCode,
                                                        pin = dealer.pin,
                                                        shopType = dealer.shopType,
                                                        phone = dealer.phone,
                                                        location = dealer.location
                                                    )
                                                    WhatsAppHelper.sendWhatsAppMessage(context, dealer.phone, msg)
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(32.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                                            ) {
                                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("WhatsApp", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Dialog when Admin taps "திருத்து"
    if (selectedDealerForEdit != null) {
        DealerProfileEditDialog(
            dealer = selectedDealerForEdit!!,
            isAdminMode = true,
            viewModel = viewModel,
            onDismiss = { selectedDealerForEdit = null }
        )
    }
}
