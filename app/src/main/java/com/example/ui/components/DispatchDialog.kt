package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.OrderEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatchDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onDispatch: (courier: String, awb: String) -> Unit
) {
    val courierOptions = listOf(
        "Shiprocket / NimbusPost (₹35–₹50 / 500g)" to "Shiprocket",
        "ST Courier (Corporate Account)" to "ST Courier",
        "DTDC Courier" to "DTDC",
        "Professional Couriers" to "Professional",
        "பேருந்து பார்சல் (ABT / KPN / ARC) - Same Day" to "Bus Parcel (ABT)"
    )

    var selectedCourier by remember { mutableStateOf(if (order.courierName.isNotBlank()) order.courierName else "ST Courier") }
    var awbNumber by remember { mutableStateOf(if (order.awbNumber.isNotBlank()) order.awbNumber else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "டெலிவரிக்கு அனுப்புதல் (Dispatch)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "${order.orderCode} • ${order.customerName} (${order.quantity} கார்டுகள்)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "கொரியர் / பார்சல் நிறுவனம் தேர்வு செய்:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    courierOptions.forEach { (label, value) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedCourier = value },
                            color = if (selectedCourier == value)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedCourier == value,
                                    onClick = { selectedCourier = value }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (selectedCourier == value) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = awbNumber,
                    onValueChange = { awbNumber = it },
                    label = { Text("AWB / டிராக்கிங் எண் / LR No") },
                    placeholder = { Text("எ.கா: ST66209418TN") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("awb_number_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDispatch(selectedCourier, awbNumber.ifBlank { "AWB-${System.currentTimeMillis() % 100000}" })
                    onDismiss()
                },
                modifier = Modifier.testTag("confirm_dispatch_btn")
            ) {
                Text("அனுப்பப்பட்டது (Mark Dispatched)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ரத்து (Cancel)")
            }
        }
    )
}
