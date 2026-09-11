package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderCode: String, // SOP format: [DEALER_ID]-[DATE]-[SERIAL_NO] (e.g. DLR01-09SEP-001)
    val dealerCode: String,
    val dealerName: String,
    val customerName: String,
    val customerPhone: String,
    val cardType: String, // "STANDARD_PVC" or "SMART_RFID"
    val accessoriesPackage: String, // Package ID
    val quantity: Int,
    val unitDealerRate: Double,
    val discountPerCard: Double,
    val totalDealerPayable: Double,
    val suggestedMrpPerCard: Double,
    val totalCustomerMrp: Double,
    val totalDealerProfit: Double,
    val status: String, // "RECEIVED", "PRINTING", "DISPATCHED", "DELIVERED"
    val courierName: String = "",
    val awbNumber: String = "",
    val notes: String = "",
    val photoUri: String = "",
    val paymentProofUri: String = "",
    // 4-Stage Quality & Packing Checklist from SOP Section 5
    val stage1BatchPrinting: Boolean = false, // நிலை 1: டீலர் வாரியாக அச்சிடுதல்
    val stage2IndividualPacking: Boolean = false, // நிலை 2: தனிநபர் கவர் (கார்டு + ஹோல்டர் + கயிறு + பெயர்)
    val stage3MasterZiplock: Boolean = false, // நிலை 3: டீலர் மாஸ்டர் ஜிப்லாக் பை + லேபிள்
    val stage4ShippingPacking: Boolean = false, // நிலை 4: பப்பில் ரேப் + வாட்டர்ப்ரூஃப் பார்சல் + ஷிப்பிங் லேபிள்
    val createdAt: Long = System.currentTimeMillis()
)
