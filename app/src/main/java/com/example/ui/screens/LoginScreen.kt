package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.DealerEntity
import com.example.ui.components.LanguageThemeQuickSwitchers
import com.example.ui.components.ReyaBrandGradient
import com.example.ui.components.ReyaLogoIcon
import com.example.ui.components.ReyaWhatsAppChatButton
import com.example.util.AppLanguage
import com.example.util.AppSettingsManager
import com.example.util.tr
import com.example.util.WhatsAppHelper
import com.example.viewmodel.CardProViewModel

enum class LoginTab(val titleTa: String, val titleEn: String) {
    ADMIN("அட்மின்", "Admin"),
    DEALER("டீலர் போர்டல்", "Dealer Portal")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: CardProViewModel
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(LoginTab.ADMIN) }
    val dealers by viewModel.allDealers.collectAsState()

    // Admin form
    var adminUsername by remember { mutableStateOf("admin") }
    var adminPassword by remember { mutableStateOf("admin") }
    var adminPassVisible by remember { mutableStateOf(false) }
    var adminError by remember { mutableStateOf<String?>(null) }

    // Dealer form
    var dealerCode by remember { mutableStateOf("DLR01") }
    var dealerPin by remember { mutableStateOf("1234") }
    var dealerPinVisible by remember { mutableStateOf(false) }
    var dealerError by remember { mutableStateOf<String?>(null) }

    val currentLang by AppSettingsManager.language.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tr("உள்நுழைவு (Login)", "Portal Login", currentLang),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Language & Theme Switchers
                LanguageThemeQuickSwitchers()
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner - Colorful Reya Identity
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
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
                                        Color(0xFF0F172A)  // Dark Cyber Slate
                                    )
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                brush = ReyaBrandGradient,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ReyaLogoIcon(size = 52.dp)

                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "REYA ID CARDS",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        brush = ReyaBrandGradient
                                    )
                                )
                                Text(
                                    text = tr("தரமான ஐடி கார்டு தயாரிப்பு & மொத்த விநியோகம்", "Premium ID Card Manufacturing & Bulk Supply", currentLang),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFF1F5F9),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tr("டீலர் நெட்வொர்க் & உடனடி ஆர்டர் போர்டல்", "Dealer Network & Instant Dispatch Portal", currentLang),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Tab Selector
            item {
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    LoginTab.values().forEach { tab ->
                        val tabTitle = if (currentLang == AppLanguage.TAMIL) tab.titleTa else tab.titleEn
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = tabTitle,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }

            // Tab Content
            if (selectedTab == LoginTab.ADMIN) {
                // Admin Login Form
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "அட்மின் உள்நுழைவு (Admin Login)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (adminError != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = adminError ?: "",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = adminUsername,
                                onValueChange = {
                                    adminUsername = it
                                    adminError = null
                                },
                                label = { Text("அட்மின் பயனர் பெயர் (Username)") },
                                placeholder = { Text("admin") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_username_input")
                            )

                            OutlinedTextField(
                                value = adminPassword,
                                onValueChange = {
                                    adminPassword = it
                                    adminError = null
                                },
                                label = { Text("அட்மின் கடவுச்சொல் (Password)") },
                                placeholder = { Text("admin") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { adminPassVisible = !adminPassVisible }) {
                                        Icon(
                                            imageVector = if (adminPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (adminPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_password_input")
                            )

                            // Quick Admin Login Button (Replacing the previous white box)
                            OutlinedButton(
                                onClick = {
                                    adminUsername = "admin"
                                    adminPassword = "admin"
                                    viewModel.loginAdmin("admin", "admin")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_quick_admin_login"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "முன்னிருப்பு அட்மின் (admin / admin) 1-கிளிக் உள்நுழை",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(
                                onClick = {
                                    val res = viewModel.loginAdmin(adminUsername, adminPassword)
                                    if (res.isFailure) {
                                        adminError = res.exceptionOrNull()?.message
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_admin_login"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("அட்மினாக உள்நுழை (Admin Portal)")
                            }
                        }
                    }
                }
            } else {
                // Dealer Login Form
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7)
                                )
                                Text(
                                    text = "டீலர் ஐடி உள்நுழைவு (Dealer Portal)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (dealerError != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = dealerError ?: "",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            // 0 Machine Investment Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFD97706),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "₹0",
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "₹0 மெஷின் முதலீடு வாக்குறுதி",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                        Text(
                                            text = "பிரிண்டர் மெஷின் முதலீடு தேவையில்லை. கார்டுக்கு ₹30 முதல் ₹105 வரை நேரடி லாபம் ஈட்டுங்கள்!",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFB45309),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            // Quick Registered Dealers Selection Chips
                            Text(
                                text = "விரைவு டீலர் தேர்வு (Quick Select):",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(dealers) { dealer ->
                                    val isSelected = dealerCode.equals(dealer.dealerCode, ignoreCase = true)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            dealerCode = dealer.dealerCode
                                            dealerPin = dealer.pin
                                            dealerError = null
                                        },
                                        label = {
                                            Text("${dealer.dealerCode} • ${dealer.name}")
                                        },
                                        leadingIcon = {
                                            if (isSelected) {
                                                Icon(Icons.Default.Check, contentDescription = null)
                                            }
                                        }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = dealerCode,
                                onValueChange = {
                                    dealerCode = it.uppercase()
                                    dealerError = null
                                },
                                label = { Text("டீலர் ஐடி (Dealer ID - e.g. DLR01, DLR02)*") },
                                placeholder = { Text("DLR01") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dealer_code_input")
                            )

                            OutlinedTextField(
                                value = dealerPin,
                                onValueChange = {
                                    dealerPin = it
                                    dealerError = null
                                },
                                label = { Text("டீலர் கடவுச்சொல் / பின் (PIN - default 1234)") },
                                placeholder = { Text("1234") },
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { dealerPinVisible = !dealerPinVisible }) {
                                        Icon(
                                            imageVector = if (dealerPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (dealerPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dealer_pin_input")
                            )

                            Button(
                                onClick = {
                                    val res = viewModel.loginDealer(dealerCode, dealerPin)
                                    if (res.isFailure) {
                                        dealerError = res.exceptionOrNull()?.message
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_dealer_login"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("டீலராக உள்நுழை (Dealer Portal)")
                            }

                            // Forgot Password WhatsApp Link to Admin
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextButton(
                                    onClick = {
                                        val msg = if (dealerCode.isNotBlank()) {
                                            "வணக்கம் அட்மின், எனது டீலர் ஐடி #${dealerCode}. எனது கடவுச்சொல்லை மறந்துவிட்டேன். தயவுசெய்து எனது கடவுச்சொல்லை அனுப்பி வைக்கவும்."
                                        } else {
                                            "வணக்கம் அட்மின், எனது டீலர் கடவுச்சொல்லை மறந்துவிட்டேன். தயவுசெய்து எனது கடவுச்சொல்லை அனுப்பி வைக்கவும்."
                                        }
                                        WhatsAppHelper.contactOfficialAdmin(context, msg)
                                    }
                                ) {
                                    Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF0284C7))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("கடவுச்சொல் மறந்துவிட்டதா? (அட்மினிடம் கேட்கவும்)", fontSize = 12.sp, color = Color(0xFF0284C7))
                                }
                            }
                        }
                    }
                }
            }

            // Official WhatsApp Chat Option
            item {
                Spacer(modifier = Modifier.height(6.dp))
                ReyaWhatsAppChatButton(compact = false)
            }

            // Footer note
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Reya ID Cards & Printing Network © 2026",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
