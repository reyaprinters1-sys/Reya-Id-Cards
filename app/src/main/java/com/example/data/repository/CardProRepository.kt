package com.example.data.repository

import com.example.data.dao.DealerDao
import com.example.data.dao.OrderDao
import com.example.data.entity.DealerEntity
import com.example.data.entity.OrderEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CardProRepository(
    private val dealerDao: DealerDao,
    private val orderDao: OrderDao
) {
    val allDealers: Flow<List<DealerEntity>> = dealerDao.getAllDealers()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    fun getOrdersByDealer(dealerCode: String): Flow<List<OrderEntity>> =
        orderDao.getOrdersByDealer(dealerCode)

    suspend fun insertOrder(order: OrderEntity): Long = orderDao.insertOrder(order)

    suspend fun updateOrder(order: OrderEntity) = orderDao.updateOrder(order)

    suspend fun deleteOrder(order: OrderEntity) = orderDao.deleteOrder(order)

    suspend fun updateOrderStatus(orderId: Long, newStatus: String) =
        orderDao.updateOrderStatus(orderId, newStatus)

    suspend fun dispatchOrder(orderId: Long, newStatus: String, courier: String, awb: String) =
        orderDao.dispatchOrder(orderId, newStatus, courier, awb)

    suspend fun updatePackingStages(orderId: Long, s1: Boolean, s2: Boolean, s3: Boolean, s4: Boolean) =
        orderDao.updatePackingStages(orderId, s1, s2, s3, s4)

    suspend fun insertDealer(dealer: DealerEntity): Long = dealerDao.insertDealer(dealer)

    suspend fun updateDealer(dealer: DealerEntity) = dealerDao.updateDealer(dealer)

    /**
     * Generates Unique Order Code per SOP Section 4:
     * Format: [DEALER_ID]-[DATE]-[SERIAL_NO]
     * Example: DLR01-09SEP-001
     */
    suspend fun generateNextOrderCode(dealerCode: String): String {
        val dateFormat = SimpleDateFormat("ddMMM", Locale.US)
        val datePart = dateFormat.format(Date()).uppercase()
        val prefix = "$dealerCode-$datePart-"
        val existingCount = orderDao.countOrdersWithPrefix(prefix)
        val nextSeq = existingCount + 1
        val serialPart = String.format(Locale.US, "%03d", nextSeq)
        return "$prefix$serialPart"
    }

    /**
     * Generates the structured file contents of [ORDER_ID]_[Customer]_Details.txt
     * adhering to SOP Section 4 File Management
     */
    fun generateOrderDetailsFileContent(order: OrderEntity): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(order.createdAt))
        return """
=====================================================
CARD PRO - ID CARD PRODUCTION ORDER DETAILS
=====================================================
Order Code     : ${order.orderCode}
Generated Date : $dateStr
Status         : ${order.status}

[DEALER DETAILS]
Dealer Code    : ${order.dealerCode}
Dealer Name    : ${order.dealerName}

[CUSTOMER DETAILS]
Customer Name  : ${order.customerName}
Mobile Number  : ${order.customerPhone}

[SPECIFICATION & PACKAGE]
Card Type      : ${if (order.cardType == "STANDARD_PVC") "Standard PVC Card" else "Smart / RFID Card"}
Package        : ${order.accessoriesPackage}
Quantity       : ${order.quantity} card(s)

[COMMERCIALS & PRICING]
Unit Dealer Rate : ₹${"%.2f".format(order.unitDealerRate)}
Bulk Discount    : ₹${"%.2f".format(order.discountPerCard)} / card
Total Dealer Pay : ₹${"%.2f".format(order.totalDealerPayable)}
Suggested MRP    : ₹${"%.2f".format(order.suggestedMrpPerCard)} / card
Total Cust MRP   : ₹${"%.2f".format(order.totalCustomerMrp)}
Dealer Profit    : ₹${"%.2f".format(order.totalDealerProfit)}

[QUALITY CHECKLIST (4-STAGE SOP)]
1. Dealer Batch Printing      : ${if (order.stage1BatchPrinting) "[X] COMPLETED" else "[ ] PENDING"}
2. Individual Polybag Packing : ${if (order.stage2IndividualPacking) "[X] COMPLETED" else "[ ] PENDING"}
3. Master Ziplock Bag Label   : ${if (order.stage3MasterZiplock) "[X] COMPLETED" else "[ ] PENDING"}
4. Shipping Bubble Parcel     : ${if (order.stage4ShippingPacking) "[X] COMPLETED" else "[ ] PENDING"}

[LOGISTICS & DISPATCH]
Courier / Transporter: ${order.courierName.ifBlank { "Not Dispatched Yet" }}
AWB / LR Track Number : ${order.awbNumber.ifBlank { "N/A" }}
Special Notes         : ${order.notes.ifBlank { "None" }}
=====================================================
""".trimIndent()
    }
}
