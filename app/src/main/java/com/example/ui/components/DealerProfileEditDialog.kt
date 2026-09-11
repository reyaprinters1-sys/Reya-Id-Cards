package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.DealerEntity
import com.example.util.WhatsAppHelper
import com.example.viewmodel.CardProViewModel
import kotlin.random.Random

/**
 * Dialog enabling Dealers to edit their profile (Phone, Address, Shop Name)
 * and change their Password/PIN.
 * Changes are saved directly to Room DB so they immediately reflect in the Admin Panel.
 */
@Composable
fun DealerProfileEditDialog(
    dealer: DealerEntity,
    isAdminMode: Boolean = false,
    viewModel: CardProViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var shopName by remember { mutableStateOf(dealer.name) }
    var shopType by remember { mutableStateOf(dealer.shopType) }
    var phone by remember { mutableStateOf(dealer.phone) }
    var location by remember { mutableStateOf(dealer.location) }

    var newPin by remember { mutableStateOf(dealer.pin) }
    var pinVisible by remember { mutableStateOf(isAdminMode) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("dialog_dealer_profile_edit"),
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
                            color = if (isAdminMode) Color(0xFF4F46E5) else Color(0xFF0284C7),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.ManageAccounts,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isAdminMode) "டீலர் விவரம் & கடவுச்சொல் திருத்து" else "சுயவிவரம் & முகவரி மாற்றம்",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "டீலர் ஐடி: #${dealer.dealerCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "மூடு")
                    }
                }

                // Sync Notice Banner
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isAdminMode)
                                "இங்கு மாற்றப்படும் விவரங்கள் மற்றும் கடவுச்சொல் உடனடியாக அமலுக்கு வரும்."
                            else
                                "நீங்கள் மாற்றும் மொபைல் எண், முகவரி மற்றும் பாஸ்வேர்ட் அட்மின் பேனலில் உடனடியாக புதுப்பிக்கப்படும்.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1E40AF),
                            fontSize = 11.sp
                        )
                    }
                }

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

                // Mobile Number Section
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        if (it.length <= 10) phone = it
                        errorMessage = null
                    },
                    label = { Text("மொபைல் எண் (Mobile Phone)*") },
                    placeholder = { Text("9842100000") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_dealer_phone")
                )

                // Location & Address Section
                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                        errorMessage = null
                    },
                    label = { Text("கடை முகவரி & டெலிவரி இடம் (Shop Address / Location)*") },
                    placeholder = { Text("எ.கா. மெயின் ரோடு, மதுரை") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_dealer_location")
                )

                // Shop Name
                OutlinedTextField(
                    value = shopName,
                    onValueChange = {
                        shopName = it
                        errorMessage = null
                    },
                    label = { Text("கடையின் பெயர் (Shop Name)*") },
                    leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Business Type
                OutlinedTextField(
                    value = shopType,
                    onValueChange = { shopType = it },
                    label = { Text("வணிக வகை (Business Type)") },
                    placeholder = { Text("இ-சேவை மையம் / ஜெராக்ஸ் / போட்டோ ஸ்டுடியோ") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Password / PIN Change Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔑 கடவுச்சொல் / பின் (Password / PIN)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            // Quick PIN Randomizer
                            Button(
                                onClick = {
                                    newPin = Random.nextInt(1000, 9999).toString()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("🎲 புதிய PIN", fontSize = 11.sp)
                            }
                        }

                        OutlinedTextField(
                            value = newPin,
                            onValueChange = {
                                newPin = it
                                errorMessage = null
                            },
                            label = { Text("உள்நுழைவு கடவுச்சொல் (Login PIN)") },
                            placeholder = { Text("1234") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { pinVisible = !pinVisible }) {
                                    Icon(
                                        imageVector = if (pinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_edit_dealer_pin")
                        )

                        Text(
                            text = "💡 கடவுச்சொல் மறந்துவிட்டால் அட்மினிடம் கேட்டுப் பெறலாம் அல்லது அட்மின் நேரடியாக புதிய PIN அமைக்கலாம்.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }
                }

                Divider()

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("ரத்து செய்")
                    }

                    Button(
                        onClick = {
                            val cleanPhone = phone.trim()
                            val cleanLoc = location.trim()
                            val cleanName = shopName.trim()
                            val cleanPin = newPin.trim()

                            if (cleanPhone.length < 10) {
                                errorMessage = "சரியான 10 இலக்க மொபைல் எண்ணை உள்ளிடவும்"
                                return@Button
                            }
                            if (cleanLoc.isEmpty()) {
                                errorMessage = "கடை முகவரியை உள்ளிடவும்"
                                return@Button
                            }
                            if (cleanName.isEmpty()) {
                                errorMessage = "கடையின் பெயரை உள்ளிடவும்"
                                return@Button
                            }
                            if (cleanPin.length < 4) {
                                errorMessage = "கடவுச்சொல் குறைந்தபட்சம் 4 இலக்கங்கள் கொண்டிருக்க வேண்டும்"
                                return@Button
                            }

                            isSaving = true
                            val updatedDealer = dealer.copy(
                                name = cleanName,
                                shopType = shopType.trim().ifBlank { dealer.shopType },
                                phone = cleanPhone,
                                location = cleanLoc,
                                pin = cleanPin
                            )

                            viewModel.updateDealerFull(
                                dealer = updatedDealer,
                                onSuccess = {
                                    isSaving = false
                                    Toast.makeText(context, "விவரங்கள் வெற்றிகரமாக சேமிக்கப்பட்டன!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                onError = { err ->
                                    isSaving = false
                                    errorMessage = err
                                }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_save_dealer_profile"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("சேமி (Save)", fontWeight = FontWeight.Bold)
                    }
                }

                // If Admin Mode, option to WhatsApp the updated credentials to the dealer
                if (isAdminMode && phone.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            val msg = WhatsAppHelper.buildDealerCredentialsMessage(
                                dealerCode = dealer.dealerCode,
                                dealerName = shopName,
                                pin = newPin
                            )
                            WhatsAppHelper.sendWhatsAppMessage(context, phone, msg)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                        border = BorderStroke(1.dp, Color(0xFF25D366))
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("புதிய ID & PIN டீலருக்கு WhatsApp-ல் அனுப்பு", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
