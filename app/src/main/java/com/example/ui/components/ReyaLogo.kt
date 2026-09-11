package com.example.ui.components

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.WhatsAppHelper

// Vibrant neon gradient palette matching the user's uploaded Reya logo
val ReyaPink = Color(0xFFF43F5E)
val ReyaViolet = Color(0xFF8B5CF6)
val ReyaCyan = Color(0xFF06B6D4)
val ReyaGold = Color(0xFFF59E0B)
val ReyaDarkBg = Color(0xFF0A0F1D)
val ReyaWhatsAppGreen = Color(0xFF25D366)

val ReyaBrandGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFEC4899), // Neon Magenta/Pink
        Color(0xFF8B5CF6), // Neon Purple
        Color(0xFF06B6D4), // Cyan
        Color(0xFFF59E0B)  // Gold Amber
    )
)

val ReyaTextGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFF43F5E),
        Color(0xFF38BDF8),
        Color(0xFFFBBF24)
    )
)

/**
 * Compact Reya Logo Icon - Replaces the bag/storefront symbol in TopAppBar
 * Displays the glowing neon 'R' circuit monogram in a sleek modern tech frame.
 */
@Composable
fun ReyaLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, RoundedCornerShape(10.dp), spotColor = ReyaCyan)
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                )
            )
            .border(
                width = 1.2.dp,
                brush = ReyaBrandGradient,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glowing futuristic monogram 'R'
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "R",
                fontSize = (size.value * 0.58).sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = ReyaBrandGradient,
                    letterSpacing = 0.sp
                )
            )
        }

        // Tech circuit node accent dot
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(3.dp)
                .size(4.dp)
                .clip(CircleShape)
                .background(ReyaCyan)
        )
    }
}

/**
 * Full Reya ID Cards Branding Header (Used in Login, About, and Welcome areas)
 */
@Composable
fun ReyaLogoHeader(
    modifier: Modifier = Modifier,
    showSubtext: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Large Glowing R Monogram Emblem
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = ReyaPink)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF2E1065), Color(0xFF0F172A)),
                        radius = 120f
                    )
                )
                .border(
                    width = 2.dp,
                    brush = ReyaBrandGradient,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "R",
                fontSize = 46.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                style = MaterialTheme.typography.displayMedium.copy(
                    brush = ReyaBrandGradient
                )
            )

            // Outer circuit connection dots
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(ReyaGold)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(ReyaCyan)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // "REYA" bold gradient title
        Text(
            text = "REYA",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp,
            style = MaterialTheme.typography.headlineMedium.copy(
                brush = ReyaBrandGradient
            )
        )

        // "ID CARDS" stylish subtext
        Text(
            text = "ID CARDS",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 5.sp,
            color = Color(0xFFE2E8F0)
        )

        if (showSubtext) {
            Text(
                text = "தரமான ஐடி கார்டு தயாரிப்பு & மொத்த விநியோகம்",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

/**
 * Official WhatsApp Contact & Direct Chat Action Widget
 * Displays the requested official phone: 7708 910 190 with instant WhatsApp launch.
 */
@Composable
fun ReyaWhatsAppChatButton(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    compact: Boolean = false
) {
    if (compact) {
        // Compact button for TopAppBar
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = ReyaWhatsAppGreen,
            modifier = modifier
                .clickable {
                    WhatsAppHelper.contactOfficialAdmin(
                        context = context,
                        message = "வணக்கம் REYA ID CARDS! ஐடி கார்டு ஆர்டர் & விநியோகம் தொடர்பாக உதவி தேவைப்படுகிறது."
                    )
                }
                .testTag("btn_top_whatsapp_chat")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "WhatsApp Chat",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "WhatsApp சேட்",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        // Full banner with direct Chat action
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = ReyaWhatsAppGreen,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Reya ID Cards நேரடி வாட்ஸ்அப்",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "அதிகாரப்பூர்வ வாட்ஸ்அப் உதவி & நேரடி சேட்",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA7F3D0)
                        )
                    }
                }

                Button(
                    onClick = {
                        WhatsAppHelper.contactOfficialAdmin(
                            context = context,
                            message = "வணக்கம் REYA ID CARDS! ஐடி கார்டு ஆர்டர் தொடர்பாக பேச விரும்புகிறேன்."
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ReyaWhatsAppGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_whatsapp_chat_now")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "சேட் செய்க",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
