package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.RateCardConfig

@Composable
fun EditRateCardDialog(
    currentConfig: RateCardConfig,
    onDismiss: () -> Unit,
    onSave: (RateCardConfig) -> Unit
) {
    // Only Card
    var onlyCardPvcDealer by remember { mutableStateOf(currentConfig.onlyCardPvcDealer.toString()) }
    var onlyCardPvcMrp by remember { mutableStateOf(currentConfig.onlyCardPvcMrp.toString()) }
    var onlyCardRfidDealer by remember { mutableStateOf(currentConfig.onlyCardRfidDealer.toString()) }
    var onlyCardRfidMrp by remember { mutableStateOf(currentConfig.onlyCardRfidMrp.toString()) }

    // Card + Case
    var casePvcDealer by remember { mutableStateOf(currentConfig.cardCasePvcDealer.toString()) }
    var casePvcMrp by remember { mutableStateOf(currentConfig.cardCasePvcMrp.toString()) }
    var caseRfidDealer by remember { mutableStateOf(currentConfig.cardCaseRfidDealer.toString()) }
    var caseRfidMrp by remember { mutableStateOf(currentConfig.cardCaseRfidMrp.toString()) }

    // Plain Lanyard Package
    var plainPvcDealer by remember { mutableStateOf(currentConfig.plainLanyardPvcDealer.toString()) }
    var plainPvcMrp by remember { mutableStateOf(currentConfig.plainLanyardPvcMrp.toString()) }
    var plainRfidDealer by remember { mutableStateOf(currentConfig.plainLanyardRfidDealer.toString()) }
    var plainRfidMrp by remember { mutableStateOf(currentConfig.plainLanyardRfidMrp.toString()) }

    // Multi-color Lanyard Package
    var multiPvcDealer by remember { mutableStateOf(currentConfig.multiLanyardPvcDealer.toString()) }
    var multiPvcMrp by remember { mutableStateOf(currentConfig.multiLanyardPvcMrp.toString()) }
    var multiRfidDealer by remember { mutableStateOf(currentConfig.multiLanyardRfidDealer.toString()) }
    var multiRfidMrp by remember { mutableStateOf(currentConfig.multiLanyardRfidMrp.toString()) }

    // Bulk Discount Slabs
    var discount11to50 by remember { mutableStateOf(currentConfig.discount11to50.toString()) }
    var discount51to200 by remember { mutableStateOf(currentConfig.discount51to200.toString()) }
    var discount200Plus by remember { mutableStateOf(currentConfig.discount200Plus.toString()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
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
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp)
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
                                text = "விலைப்பட்டியல் திருத்தம் (Edit Rate Card)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "அட்மின் பிரத்தியேக கட்டண மேலாண்மை",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Package 1: Only Card
                    item {
                        PackageRateEditorCard(
                            packageName = "1. Only Card (கார்டு மட்டும் - இருபுற வண்ணம்)",
                            pvcDealer = onlyCardPvcDealer,
                            onPvcDealerChange = { onlyCardPvcDealer = it },
                            pvcMrp = onlyCardPvcMrp,
                            onPvcMrpChange = { onlyCardPvcMrp = it },
                            rfidDealer = onlyCardRfidDealer,
                            onRfidDealerChange = { onlyCardRfidDealer = it },
                            rfidMrp = onlyCardRfidMrp,
                            onRfidMrpChange = { onlyCardRfidMrp = it }
                        )
                    }

                    // Package 2: Card + Case
                    item {
                        PackageRateEditorCard(
                            packageName = "2. Card + Case (கார்டு + பவுச் / ஹோல்டர்)",
                            pvcDealer = casePvcDealer,
                            onPvcDealerChange = { casePvcDealer = it },
                            pvcMrp = casePvcMrp,
                            onPvcMrpChange = { casePvcMrp = it },
                            rfidDealer = caseRfidDealer,
                            onRfidDealerChange = { caseRfidDealer = it },
                            rfidMrp = caseRfidMrp,
                            onRfidMrpChange = { caseRfidMrp = it }
                        )
                    }

                    // Package 3: Plain Lanyard Package
                    item {
                        PackageRateEditorCard(
                            packageName = "3. Card + Case + Plain Lanyard (சாதாரண கயிறு)",
                            pvcDealer = plainPvcDealer,
                            onPvcDealerChange = { plainPvcDealer = it },
                            pvcMrp = plainPvcMrp,
                            onPvcMrpChange = { plainPvcMrp = it },
                            rfidDealer = plainRfidDealer,
                            onRfidDealerChange = { plainRfidDealer = it },
                            rfidMrp = plainRfidMrp,
                            onRfidMrpChange = { plainRfidMrp = it }
                        )
                    }

                    // Package 4: Multi-color Lanyard Package
                    item {
                        PackageRateEditorCard(
                            packageName = "4. Card + Case + Multi-color Lanyard (பிராண்டட் கயிறு)",
                            pvcDealer = multiPvcDealer,
                            onPvcDealerChange = { multiPvcDealer = it },
                            pvcMrp = multiPvcMrp,
                            onPvcMrpChange = { multiPvcMrp = it },
                            rfidDealer = multiRfidDealer,
                            onRfidDealerChange = { multiRfidDealer = it },
                            rfidMrp = multiRfidMrp,
                            onRfidMrpChange = { multiRfidMrp = it }
                        )
                    }

                    // Bulk Discount Slabs
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "பல்க் ஆர்டர் தள்ளுபடி சலுகைகள் (Bulk Discount Slabs)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "அதிக எண்ணிக்கையில் ஆர்டர் செய்யும் போது கார்டுக்கு கழிக்கப்படும் தொகை",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF475569),
                                    fontSize = 12.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = discount11to50,
                                        onValueChange = { discount11to50 = it },
                                        label = { Text("11-50 (₹)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = discount51to200,
                                        onValueChange = { discount51to200 = it },
                                        label = { Text("51-200 (₹)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = discount200Plus,
                                        onValueChange = { discount200Plus = it },
                                        label = { Text("200+ (₹)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp))

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            val def = RateCardConfig()
                            onlyCardPvcDealer = def.onlyCardPvcDealer.toString()
                            onlyCardPvcMrp = def.onlyCardPvcMrp.toString()
                            onlyCardRfidDealer = def.onlyCardRfidDealer.toString()
                            onlyCardRfidMrp = def.onlyCardRfidMrp.toString()

                            casePvcDealer = def.cardCasePvcDealer.toString()
                            casePvcMrp = def.cardCasePvcMrp.toString()
                            caseRfidDealer = def.cardCaseRfidDealer.toString()
                            caseRfidMrp = def.cardCaseRfidMrp.toString()

                            plainPvcDealer = def.plainLanyardPvcDealer.toString()
                            plainPvcMrp = def.plainLanyardPvcMrp.toString()
                            plainRfidDealer = def.plainLanyardRfidDealer.toString()
                            plainRfidMrp = def.plainLanyardRfidMrp.toString()

                            multiPvcDealer = def.multiLanyardPvcDealer.toString()
                            multiPvcMrp = def.multiLanyardPvcMrp.toString()
                            multiRfidDealer = def.multiLanyardRfidDealer.toString()
                            multiRfidMrp = def.multiLanyardRfidMrp.toString()

                            discount11to50 = def.discount11to50.toString()
                            discount51to200 = def.discount51to200.toString()
                            discount200Plus = def.discount200Plus.toString()
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("முன்னிருப்பு SOP விலை")
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onDismiss) {
                            Text("ரத்து")
                        }
                        Button(
                            onClick = {
                                val updated = RateCardConfig(
                                    onlyCardPvcDealer = onlyCardPvcDealer.toDoubleOrNull() ?: 30.0,
                                    onlyCardPvcMrp = onlyCardPvcMrp.toDoubleOrNull() ?: 60.0,
                                    onlyCardRfidDealer = onlyCardRfidDealer.toDoubleOrNull() ?: 65.0,
                                    onlyCardRfidMrp = onlyCardRfidMrp.toDoubleOrNull() ?: 120.0,

                                    cardCasePvcDealer = casePvcDealer.toDoubleOrNull() ?: 40.0,
                                    cardCasePvcMrp = casePvcMrp.toDoubleOrNull() ?: 80.0,
                                    cardCaseRfidDealer = caseRfidDealer.toDoubleOrNull() ?: 75.0,
                                    cardCaseRfidMrp = caseRfidMrp.toDoubleOrNull() ?: 140.0,

                                    plainLanyardPvcDealer = plainPvcDealer.toDoubleOrNull() ?: 55.0,
                                    plainLanyardPvcMrp = plainPvcMrp.toDoubleOrNull() ?: 110.0,
                                    plainLanyardRfidDealer = plainRfidDealer.toDoubleOrNull() ?: 90.0,
                                    plainLanyardRfidMrp = plainRfidMrp.toDoubleOrNull() ?: 170.0,

                                    multiLanyardPvcDealer = multiPvcDealer.toDoubleOrNull() ?: 85.0,
                                    multiLanyardPvcMrp = multiPvcMrp.toDoubleOrNull() ?: 160.0,
                                    multiLanyardRfidDealer = multiRfidDealer.toDoubleOrNull() ?: 125.0,
                                    multiLanyardRfidMrp = multiRfidMrp.toDoubleOrNull() ?: 230.0,

                                    discount11to50 = discount11to50.toDoubleOrNull() ?: 3.0,
                                    discount51to200 = discount51to200.toDoubleOrNull() ?: 5.0,
                                    discount200Plus = discount200Plus.toDoubleOrNull() ?: 8.0
                                )
                                onSave(updated)
                            },
                            modifier = Modifier.testTag("btn_save_rate_card")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("விலை சேமி (Save)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PackageRateEditorCard(
    packageName: String,
    pvcDealer: String,
    onPvcDealerChange: (String) -> Unit,
    pvcMrp: String,
    onPvcMrpChange: (String) -> Unit,
    rfidDealer: String,
    onRfidDealerChange: (String) -> Unit,
    rfidMrp: String,
    onRfidMrpChange: (String) -> Unit
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = packageName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // PVC Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.width(90.dp)
                ) {
                    Text(
                        text = "PVC கார்டு",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0369A1),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }

                OutlinedTextField(
                    value = pvcDealer,
                    onValueChange = onPvcDealerChange,
                    label = { Text("டீலர் விலை (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = pvcMrp,
                    onValueChange = onPvcMrpChange,
                    label = { Text("MRP விலை (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // RFID Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFEDE9FE),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.width(90.dp)
                ) {
                    Text(
                        text = "RFID கார்டு",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D28D9),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }

                OutlinedTextField(
                    value = rfidDealer,
                    onValueChange = onRfidDealerChange,
                    label = { Text("டீலர் விலை (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = rfidMrp,
                    onValueChange = onRfidMrpChange,
                    label = { Text("MRP விலை (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
