package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.util.WhatsAppHelper
import kotlin.random.Random

@Composable
fun AddDealerDialog(
    initialDealerCode: String = "DLR05",
    initialPin: String = (1000..9999).random().toString(),
    onDismiss: () -> Unit,
    onSave: (code: String, name: String, shopType: String, phone: String, location: String, pin: String) -> Unit,
    onSaveAndWhatsApp: ((code: String, name: String, shopType: String, phone: String, location: String, pin: String) -> Unit)? = null
) {
    val context = LocalContext.current
    var dealerCode by remember { mutableStateOf(initialDealerCode) }
    var shopName by remember { mutableStateOf("") }
    var shopType by remember { mutableStateOf("இ-சேவை & ஜெராக்ஸ் (E-Sevai & Xerox)") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf(initialPin) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun generateNewPin(): String {
        return Random.nextInt(1000, 9999).toString()
    }

    fun generateNextCode(): String {
        val num = Random.nextInt(5, 99)
        return "DLR" + num.toString().padStart(2, '0')
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
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
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "டீலர் பதிவு & பாஸ்வேர்ட் ஜெனரேட்டர்",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "WhatsApp Business மூலம் அனுப்பும் வசதி",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp
                        )
                    }
                }

                Divider()

                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Section: Dealer ID Generator
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1. டீலர் ஐடி (Dealer ID)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(
                                onClick = { dealerCode = generateNextCode() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("🔄 புதிய ஐடி உருவாக்கு", fontSize = 11.sp)
                            }
                        }

                        OutlinedTextField(
                            value = dealerCode,
                            onValueChange = { dealerCode = it.uppercase() },
                            placeholder = { Text("DLR05") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_new_dealer_code")
                        )
                    }
                }

                // Section: Password / PIN Generator
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. கடவுச்சொல் / பின் (Password / PIN)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Button(
                                onClick = { pin = generateNewPin() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("🎲 PIN ஜெனரேட்டர்", fontSize = 11.sp)
                            }
                        }

                        OutlinedTextField(
                            value = pin,
                            onValueChange = { pin = it },
                            label = { Text("உள்நுழைவு கடவுச்சொல் / பின் (PIN)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Shop Details
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("கடை / நிறுவனத்தின் பெயர் (Shop Name)*") },
                    placeholder = { Text("எ.கா. சிவா போட்டோ ஸ்டுடியோ") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_new_dealer_name")
                )

                OutlinedTextField(
                    value = shopType,
                    onValueChange = { shopType = it },
                    label = { Text("வணிக வகை (Business Type)") },
                    placeholder = { Text("இ-சேவை / ஜெராக்ஸ் / ஸ்டுடியோ / ஸ்டேஷனரி") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10) phone = it },
                    label = { Text("டீலர் WhatsApp மொபைல் எண் (Phone Number)*") },
                    placeholder = { Text("98421XXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("ஊர் / மாவட்டம் (Location)") },
                    placeholder = { Text("எ.கா. மதுரை, திருநெல்வேலி, தேனி") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Actions: Save & Send WhatsApp
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Action 1: Save & WhatsApp via 7708 910 190
                    Button(
                        onClick = {
                            if (dealerCode.isBlank() || shopName.isBlank()) {
                                errorMessage = "டீலர் ஐடி மற்றும் கடையின் பெயர் கட்டாயம்!"
                            } else {
                                val c = dealerCode.trim()
                                val n = shopName.trim()
                                val t = shopType.trim()
                                val p = phone.trim()
                                val l = location.trim()
                                val pw = pin.trim().ifBlank { "1234" }

                                onSave(c, n, t, p, l, pw)

                                // Launch WhatsApp welcome message to dealer
                                val msg = WhatsAppHelper.buildDealerCredentialsMessage(
                                    dealerName = n,
                                    dealerCode = c,
                                    pin = pw,
                                    shopType = t,
                                    phone = p,
                                    location = l
                                )
                                WhatsAppHelper.sendWhatsAppMessage(context, p, msg)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_save_and_whatsapp_dealer")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "வாட்ஸ்அப்பில் ஐடி & PIN அனுப்பு",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Action 2: Save Only or Cancel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("ரத்து")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                if (dealerCode.isBlank() || shopName.isBlank()) {
                                    errorMessage = "டீலர் ஐடி மற்றும் கடையின் பெயர் கட்டாயம்!"
                                } else {
                                    onSave(
                                        dealerCode.trim(),
                                        shopName.trim(),
                                        shopType.trim(),
                                        phone.trim(),
                                        location.trim(),
                                        pin.trim().ifBlank { "1234" }
                                    )
                                }
                            },
                            modifier = Modifier.testTag("btn_save_dealer")
                        ) {
                            Text("சேமி மட்டும் (Save Only)")
                        }
                    }
                }
            }
        }
    }
}
