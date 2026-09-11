package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dealers")
data class DealerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dealerCode: String, // e.g. "DLR01", "DLR-7842"
    val name: String, // e.g. "Reya Printers"
    val shopType: String, // "E-Sevai", "Xerox & Browsing", "Photo Studio", "Stationery & Printers"
    val phone: String,
    val location: String,
    val upiId: String = "reyaprinters@upi",
    val starterKitDelivered: Boolean = true,
    val pin: String = "1234", // Dealer login PIN/password
    val createdAt: Long = System.currentTimeMillis()
)
