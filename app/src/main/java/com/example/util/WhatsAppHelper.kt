package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WhatsAppHelper {
    const val OFFICIAL_WHATSAPP_NUMBER = "7708910190"
    const val OFFICIAL_WHATSAPP_DISPLAY = "7708 910 190"
    const val OFFICIAL_WHATSAPP_INTERNATIONAL = "917708910190"
    const val OFFICIAL_BUSINESS_NAME = "REYA ID CARDS"

    /**
     * Opens WhatsApp to send a pre-formatted message to the specified recipient phone number.
     */
    fun sendWhatsAppMessage(context: Context, recipientPhone: String, messageText: String) {
        try {
            val digitsOnly = recipientPhone.replace(Regex("[^0-9]"), "")
            val formattedPhone = when {
                digitsOnly.startsWith("91") && digitsOnly.length == 12 -> digitsOnly
                digitsOnly.length == 10 -> "91$digitsOnly"
                else -> digitsOnly
            }
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(messageText)}")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp திறக்க இயலவில்லை: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Direct one-tap contact to the official WhatsApp Business Admin number (7708 910 190).
     */
    fun contactOfficialAdmin(context: Context, message: String = "வணக்கம் REYA ID CARDS! ஐடி கார்டு ஆர்டர் & விநியோகம் தொடர்பாக பேச விரும்புகிறேன்.") {
        sendWhatsAppMessage(context, OFFICIAL_WHATSAPP_NUMBER, message)
    }

    /**
     * Shares the daily operations report directly to WhatsApp.
     */
    fun shareReportViaWhatsApp(context: Context, reportText: String, recipientPhone: String? = null) {
        if (!recipientPhone.isNullOrBlank()) {
            sendWhatsAppMessage(context, recipientPhone, reportText)
            return
        }
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_TEXT, reportText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            try {
                val chooserIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, reportText)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(chooserIntent, "அறிக்கையை பகிரவும் (Share Report)").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } catch (ex: Exception) {
                sendWhatsAppMessage(context, OFFICIAL_WHATSAPP_NUMBER, reportText)
            }
        }
    }

    /**
     * Formats a welcome message containing dealer login credentials.
     */
    fun buildDealerCredentialsMessage(
        dealerName: String,
        dealerCode: String,
        pin: String,
        shopType: String = "டீலர் பார்ட்னர்",
        phone: String = "",
        location: String = ""
    ): String {
        return """
வணக்கம் $dealerName! 
REYA ID CARDS நெட்வொர்க்கில் தங்களை அன்போடு வரவேற்கிறோம்! 🤝

தங்களின் பிரத்தியேக போர்ட்டல் உள்நுழைவு விவரங்கள்:
🆔 டீலர் ஐடி (Dealer ID): $dealerCode
🔑 கடவுச்சொல் / பின் (PIN): $pin
📱 பதிவு எண்: $phone
🏬 கடை வகை: $shopType
📍 ஊர்: $location

✨ ₹0 மெஷின் முதலீடு சிறப்பு பலன்:
பிரிண்டர் மெஷின் தேவையில்லை! கச்சா பொருள் செலவில்லை! வாடிக்கையாளர் ஆர்டர்களை மட்டும் எடுத்து ஆப்பில் பதிவு செய்யுங்கள். கார்டுக்கு ₹30 முதல் ₹105 வரை நேரடி லாபம் உங்களுக்கே! தயாரிப்பு & நேரடி டெலிவரி எங்களது பொறுப்பு.

🏢 REYA ID CARDS அதிகாரப்பூர்வ WhatsApp: +91 $OFFICIAL_WHATSAPP_DISPLAY
ஆர்டர் & உற்பத்தி தொடர்பான விசாரணைகளுக்கு தொடர்பு கொள்ளவும்.
""".trimIndent()
    }
}
