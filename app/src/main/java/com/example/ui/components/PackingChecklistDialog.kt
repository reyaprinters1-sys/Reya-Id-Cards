package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.outlined.Circle
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

@Composable
fun PackingChecklistDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onSave: (s1: Boolean, s2: Boolean, s3: Boolean, s4: Boolean) -> Unit
) {
    var stage1 by remember { mutableStateOf(order.stage1BatchPrinting) }
    var stage2 by remember { mutableStateOf(order.stage2IndividualPacking) }
    var stage3 by remember { mutableStateOf(order.stage3MasterZiplock) }
    var stage4 by remember { mutableStateOf(order.stage4ShippingPacking) }

    val allCompleted = stage1 && stage2 && stage3 && stage4

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = "பேக்கிங் தரக் கட்டுப்பாடு (SOP)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${order.orderCode} • ${order.customerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "அச்சிடப்பட்ட கார்டுகளைப் பிழையின்றி பேக் செய்ய 4 கட்டப் பணிப்பாய்வு:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                // Stage 1
                ChecklistStepItem(
                    stepNum = 1,
                    title = "நிலை 1: டீலர் வாரியாக அச்சிடுதல்",
                    subtitle = "ஒவ்வொரு டீலரின் கார்டுகளையும் தனித்தனி பேட்ச்களாக பிரிண்ட் செய்தல்",
                    checked = stage1,
                    onCheckedChange = { stage1 = it },
                    tag = "check_stage_1"
                )

                // Stage 2
                ChecklistStepItem(
                    stepNum = 2,
                    title = "நிலை 2: தனிநபர் கவர் (Individual Packing)",
                    subtitle = "கார்டு + ஹோல்டர் + கயிறு பாலிதீன் கவரில் போட்டு, பெயர் மட்டும் ஒட்டுதல்",
                    checked = stage2,
                    onCheckedChange = { stage2 = it },
                    tag = "check_stage_2"
                )

                // Stage 3
                ChecklistStepItem(
                    stepNum = 3,
                    title = "நிலை 3: டீலர் மாஸ்டர் ஜிப்லாக் பை",
                    subtitle = "டீலரின் அனைத்து கார்டுகளையும் பெரிய ஜிப்லாக் பையில் போட்டு லேபிள் ஒட்டுதல்",
                    checked = stage3,
                    onCheckedChange = { stage3 = it },
                    tag = "check_stage_3"
                )

                // Stage 4
                ChecklistStepItem(
                    stepNum = 4,
                    title = "நிலை 4: ஷிப்பிங் பார்சல் பேக்கிங்",
                    subtitle = "பப்பில் ரேப் (Bubble wrap) சுற்றி, வாட்டர்ப்ரூஃப் பார்சலில் AWB லேபிள் ஒட்டுதல்",
                    checked = stage4,
                    onCheckedChange = { stage4 = it },
                    tag = "check_stage_4"
                )

                if (allCompleted) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD1FAE5)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669)
                            )
                            Text(
                                text = "அனைத்து 4 நிலைகளும் சரிபார்க்கப்பட்டன! பார்சல் டெலிவரிக்கு தயார்.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(stage1, stage2, stage3, stage4)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_checklist_btn")
            ) {
                Text("சேமி (Save)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("மூடு (Close)")
            }
        }
    )
}

@Composable
private fun ChecklistStepItem(
    stepNum: Int,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (checked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
