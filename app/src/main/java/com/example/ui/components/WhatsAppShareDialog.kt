package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OrderEntity

@Composable
fun WhatsAppShareDialog(
    order: OrderEntity,
    customerMessage: String,
    dealerMessage: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var sendToCustomer by remember { mutableStateOf(true) }

    val activeMessage = if (sendToCustomer) customerMessage else dealerMessage
    val targetPhone = if (sendToCustomer) order.customerPhone else ""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color(0xFF25D366),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "WhatsApp தானியங்கி மெசேஜ்",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ஆர்டர் நிலவர உடனடி அறிவிப்பு",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = sendToCustomer,
                        onClick = { sendToCustomer = true },
                        label = { Text("வாடிக்கையாளருக்கு (Customer)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !sendToCustomer,
                        onClick = { sendToCustomer = false },
                        label = { Text("டீலருக்கு (Dealer)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "அனுப்பப்பட உள்ள செய்தி:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE7F8ED))
                        .padding(10.dp)
                ) {
                    Text(
                        text = activeMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF0F5132),
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    sendWhatsApp(context, targetPhone, activeMessage)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366),
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("send_whatsapp_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("WhatsApp-ல் அனுப்பு")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ரத்து (Cancel)")
            }
        }
    )
}

private fun sendWhatsApp(context: Context, phone: String, message: String) {
    try {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        val formattedPhone = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
        val uri = if (formattedPhone.isNotBlank()) {
            Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}")
        } else {
            Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(message)}")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to standard share sheet
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(shareIntent, "செய்தியைப் பகிரவும் (Share Message)"))
        } catch (ex: Exception) {
            Toast.makeText(context, "WhatsApp திறக்க இயலவில்லை (Could not open WhatsApp)", Toast.LENGTH_SHORT).show()
        }
    }
}
