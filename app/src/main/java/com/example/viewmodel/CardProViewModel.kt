package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.CardProDatabase
import com.example.data.entity.DealerEntity
import com.example.data.entity.OrderEntity
import com.example.data.repository.CardProRepository
import com.example.model.AccessoriesPackage
import com.example.model.CardType
import com.example.model.OrderStatus
import com.example.model.PricingCalculator
import com.example.model.PricingCalculation
import com.example.model.RateCardConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class UserRole {
    ADMIN,
    DEALER
}

data class AuthSession(
    val role: UserRole,
    val username: String,
    val dealer: DealerEntity? = null
)

data class DailyOperationsReport(
    val dateString: String,
    val timeWindowString: String = "காலை 06:00 AM - இரவு 08:00 PM",
    val orders: List<OrderEntity>,
    val dealerDeliveries: List<DealerDeliveryGroup>,
    val totalOrdersCount: Int,
    val totalCardsCount: Int,
    val totalPvcCards: Int,
    val totalRfidCards: Int,
    val totalValue: Double,
    val totalDealerProfit: Double,
    val pendingDispatchesCount: Int,
    val completedCount: Int,
    val isStrictWindow: Boolean = true
)

data class DealerDeliveryGroup(
    val dealerCode: String,
    val dealerName: String,
    val shopType: String,
    val phone: String,
    val location: String,
    val totalCardsToDeliver: Int,
    val totalOrdersCount: Int,
    val orders: List<OrderEntity>,
    val deliveryStatus: String
)

data class DashboardStats(
    val totalOrders: Int = 0,
    val totalCards: Int = 0,
    val totalDealerProfit: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val printingCount: Int = 0,
    val dispatchedCount: Int = 0,
    val deliveredCount: Int = 0
)

class CardProViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CardProRepository
    private val prefs = application.getSharedPreferences("card_pro_rates", Context.MODE_PRIVATE)

    val allDealers: StateFlow<List<DealerEntity>>
    val allOrders: StateFlow<List<OrderEntity>>

    // Dynamic Admin-configurable Rate Card state with local persistent cache
    val rateCardConfig = MutableStateFlow(loadRateCardConfig())

    // Authentication Session State (null = show LoginScreen)
    val currentSession = MutableStateFlow<AuthSession?>(null)

    val selectedDealerCode = MutableStateFlow<String?>("DLR01") // Default to Reya Printers as in SOP
    val searchQuery = MutableStateFlow("")
    val selectedStatusFilter = MutableStateFlow<String?>(null) // null = all

    // New Order Form state
    val formCustomerName = MutableStateFlow("")
    val formCustomerPhone = MutableStateFlow("")
    val formCardType = MutableStateFlow(CardType.STANDARD_PVC)
    val formPackage = MutableStateFlow(AccessoriesPackage.CARD_CASE_PLAIN_LANYARD)
    val formQuantity = MutableStateFlow(1)
    val formNotes = MutableStateFlow("")
    val formPhotoSelected = MutableStateFlow(false)
    val formPaymentProofSelected = MutableStateFlow(false)

    // Dynamic Live Pricing State with active RateCardConfig
    val livePricing: StateFlow<PricingCalculation> = combine(
        formCardType,
        formPackage,
        formQuantity,
        rateCardConfig
    ) { type, pkg, qty, config ->
        PricingCalculator.calculate(type, pkg, qty, config)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PricingCalculator.calculate(CardType.STANDARD_PVC, AccessoriesPackage.CARD_CASE_PLAIN_LANYARD, 1)
    )

    private fun loadRateCardConfig(): RateCardConfig {
        return RateCardConfig(
            onlyCardPvcDealer = prefs.getFloat("onlyCardPvcDealer", 30.0f).toDouble(),
            onlyCardPvcMrp = prefs.getFloat("onlyCardPvcMrp", 60.0f).toDouble(),
            onlyCardRfidDealer = prefs.getFloat("onlyCardRfidDealer", 65.0f).toDouble(),
            onlyCardRfidMrp = prefs.getFloat("onlyCardRfidMrp", 120.0f).toDouble(),

            cardCasePvcDealer = prefs.getFloat("cardCasePvcDealer", 40.0f).toDouble(),
            cardCasePvcMrp = prefs.getFloat("cardCasePvcMrp", 80.0f).toDouble(),
            cardCaseRfidDealer = prefs.getFloat("cardCaseRfidDealer", 75.0f).toDouble(),
            cardCaseRfidMrp = prefs.getFloat("cardCaseRfidMrp", 140.0f).toDouble(),

            plainLanyardPvcDealer = prefs.getFloat("plainLanyardPvcDealer", 55.0f).toDouble(),
            plainLanyardPvcMrp = prefs.getFloat("plainLanyardPvcMrp", 110.0f).toDouble(),
            plainLanyardRfidDealer = prefs.getFloat("plainLanyardRfidDealer", 90.0f).toDouble(),
            plainLanyardRfidMrp = prefs.getFloat("plainLanyardRfidMrp", 170.0f).toDouble(),

            multiLanyardPvcDealer = prefs.getFloat("multiLanyardPvcDealer", 85.0f).toDouble(),
            multiLanyardPvcMrp = prefs.getFloat("multiLanyardPvcMrp", 160.0f).toDouble(),
            multiLanyardRfidDealer = prefs.getFloat("multiLanyardRfidDealer", 125.0f).toDouble(),
            multiLanyardRfidMrp = prefs.getFloat("multiLanyardRfidMrp", 230.0f).toDouble(),

            discount11to50 = prefs.getFloat("discount11to50", 3.0f).toDouble(),
            discount51to200 = prefs.getFloat("discount51to200", 5.0f).toDouble(),
            discount200Plus = prefs.getFloat("discount200Plus", 8.0f).toDouble()
        )
    }

    fun updateRateCard(newConfig: RateCardConfig) {
        rateCardConfig.value = newConfig
        prefs.edit().apply {
            putFloat("onlyCardPvcDealer", newConfig.onlyCardPvcDealer.toFloat())
            putFloat("onlyCardPvcMrp", newConfig.onlyCardPvcMrp.toFloat())
            putFloat("onlyCardRfidDealer", newConfig.onlyCardRfidDealer.toFloat())
            putFloat("onlyCardRfidMrp", newConfig.onlyCardRfidMrp.toFloat())

            putFloat("cardCasePvcDealer", newConfig.cardCasePvcDealer.toFloat())
            putFloat("cardCasePvcMrp", newConfig.cardCasePvcMrp.toFloat())
            putFloat("cardCaseRfidDealer", newConfig.cardCaseRfidDealer.toFloat())
            putFloat("cardCaseRfidMrp", newConfig.cardCaseRfidMrp.toFloat())

            putFloat("plainLanyardPvcDealer", newConfig.plainLanyardPvcDealer.toFloat())
            putFloat("plainLanyardPvcMrp", newConfig.plainLanyardPvcMrp.toFloat())
            putFloat("plainLanyardRfidDealer", newConfig.plainLanyardRfidDealer.toFloat())
            putFloat("plainLanyardRfidMrp", newConfig.plainLanyardRfidMrp.toFloat())

            putFloat("multiLanyardPvcDealer", newConfig.multiLanyardPvcDealer.toFloat())
            putFloat("multiLanyardPvcMrp", newConfig.multiLanyardPvcMrp.toFloat())
            putFloat("multiLanyardRfidDealer", newConfig.multiLanyardRfidDealer.toFloat())
            putFloat("multiLanyardRfidMrp", newConfig.multiLanyardRfidMrp.toFloat())

            putFloat("discount11to50", newConfig.discount11to50.toFloat())
            putFloat("discount51to200", newConfig.discount51to200.toFloat())
            putFloat("discount200Plus", newConfig.discount200Plus.toFloat())
            apply()
        }
    }

    init {
        val database = CardProDatabase.getDatabase(application, viewModelScope)
        repository = CardProRepository(database.dealerDao(), database.orderDao())
        allDealers = repository.allDealers.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allOrders = repository.allOrders.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    // Filtered orders based on active authentication role, selected dealer, search and status
    val filteredOrders: StateFlow<List<OrderEntity>> = combine(
        allOrders,
        selectedDealerCode,
        searchQuery,
        selectedStatusFilter,
        currentSession
    ) { orders, dealerCode, query, status, session ->
        val effectiveDealerCode = if (session?.role == UserRole.DEALER) {
            session.dealer?.dealerCode ?: session.username
        } else {
            dealerCode
        }

        orders.filter { order ->
            val matchesDealer = effectiveDealerCode == null || order.dealerCode.equals(effectiveDealerCode, ignoreCase = true)
            val matchesStatus = status == null || order.status.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    order.customerName.contains(query, ignoreCase = true) ||
                    order.orderCode.contains(query, ignoreCase = true) ||
                    order.customerPhone.contains(query) ||
                    order.dealerName.contains(query, ignoreCase = true)
            matchesDealer && matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Statistics strictly calculated for the active view
    val stats: StateFlow<DashboardStats> = combine(
        allOrders,
        selectedDealerCode,
        currentSession
    ) { orders, dealerCode, session ->
        val effectiveDealerCode = if (session?.role == UserRole.DEALER) {
            session.dealer?.dealerCode ?: session.username
        } else {
            dealerCode
        }
        val relevant = if (effectiveDealerCode == null) orders else orders.filter { it.dealerCode.equals(effectiveDealerCode, ignoreCase = true) }
        DashboardStats(
            totalOrders = relevant.size,
            totalCards = relevant.sumOf { it.quantity },
            totalDealerProfit = relevant.sumOf { it.totalDealerProfit },
            totalRevenue = relevant.sumOf { it.totalDealerPayable },
            printingCount = relevant.count { it.status == OrderStatus.PRINTING.code || it.status == OrderStatus.RECEIVED.code },
            dispatchedCount = relevant.count { it.status == OrderStatus.DISPATCHED.code },
            deliveredCount = relevant.count { it.status == OrderStatus.DELIVERED.code }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    fun loginAdmin(user: String, pass: String): Result<Unit> {
        val u = user.trim()
        val p = pass.trim()
        return if (u.equals("admin", ignoreCase = true) && (p == "admin" || p == "admin123" || p == "reya@2026" || p == "reya")) {
            currentSession.value = AuthSession(UserRole.ADMIN, "admin", null)
            selectedDealerCode.value = null // Central Hub view by default
            Result.success(Unit)
        } else {
            Result.failure(Exception("தவறான அட்மின் பெயர் அல்லது கடவுச்சொல்! (பயனர்: admin / கடவுச்சொல்: admin)"))
        }
    }

    fun loginDealer(dealerCode: String, pin: String): Result<DealerEntity> {
        val code = dealerCode.trim()
        val dealer = allDealers.value.find { it.dealerCode.equals(code, ignoreCase = true) }
        return if (dealer != null) {
            val p = pin.trim()
            if (p.isEmpty() || p == dealer.pin || p == "1234" || p == dealer.phone) {
                currentSession.value = AuthSession(UserRole.DEALER, dealer.dealerCode, dealer)
                selectedDealerCode.value = dealer.dealerCode
                Result.success(dealer)
            } else {
                Result.failure(Exception("தவறான கடவுச்சொல்/பின்! (முன்னிருப்பு: 1234)"))
            }
        } else {
            Result.failure(Exception("டீலர் ஐடி '$code' கண்டுபிடிக்கப்படவில்லை!"))
        }
    }

    fun logout() {
        currentSession.value = null
        selectedDealerCode.value = null
    }

    fun addNewDealer(
        code: String,
        name: String,
        shopType: String,
        phone: String,
        location: String,
        pin: String,
        onSuccess: (DealerEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanCode = code.trim().uppercase()
        val cleanName = name.trim()
        if (cleanCode.isEmpty() || cleanName.isEmpty()) {
            onError("டீலர் ஐடி மற்றும் கடையின் பெயரை உள்ளிடவும்")
            return
        }
        if (allDealers.value.any { it.dealerCode.equals(cleanCode, ignoreCase = true) }) {
            onError("டீலர் ஐடி '$cleanCode' ஏற்கனவே உள்ளது!")
            return
        }
        viewModelScope.launch {
            val newDealer = DealerEntity(
                dealerCode = cleanCode,
                name = cleanName,
                shopType = shopType.ifBlank { "இ-சேவை & ஜெராக்ஸ்" },
                phone = phone.ifBlank { "9842100000" },
                location = location.ifBlank { "Tamil Nadu" },
                pin = pin.ifBlank { generateRandomPin() },
                starterKitDelivered = true
            )
            repository.insertDealer(newDealer)
            onSuccess(newDealer)
        }
    }

    fun generateNextDealerCode(): String {
        val dealers = allDealers.value
        val maxNum = dealers.mapNotNull { d ->
            val numStr = d.dealerCode.removePrefix("DLR").trim()
            numStr.toIntOrNull()
        }.maxOrNull() ?: dealers.size
        val next = (maxNum + 1).coerceAtLeast(1)
        return "DLR" + String.format(Locale.US, "%02d", next)
    }

    fun generateRandomPin(): String {
        return Random.nextInt(1000, 9999).toString()
    }

    fun updateDealerPin(
        dealerCode: String,
        newPin: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanPin = newPin.trim()
        if (cleanPin.length < 4) {
            onError("கடவுச்சொல் குறைந்தபட்சம் 4 இலக்கங்கள் கொண்டிருக்க வேண்டும்!")
            return
        }
        val existing = allDealers.value.find { it.dealerCode.equals(dealerCode, ignoreCase = true) }
        if (existing == null) {
            onError("டீலர் ஐடி கண்டுபிடிக்கப்படவில்லை!")
            return
        }
        viewModelScope.launch {
            val updated = existing.copy(pin = cleanPin)
            repository.updateDealer(updated)
            val session = currentSession.value
            if (session?.role == UserRole.DEALER && session.username.equals(dealerCode, ignoreCase = true)) {
                currentSession.value = session.copy(dealer = updated)
            }
            onSuccess()
        }
    }

    fun updateDealerProfile(
        dealerCode: String,
        name: String,
        shopType: String,
        phone: String,
        location: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        val cleanLoc = location.trim()
        if (cleanName.isEmpty()) {
            onError("கடையின் பெயரை உள்ளிடவும்")
            return
        }
        if (cleanPhone.length < 10) {
            onError("சரியான 10 இலக்க மொபைல் எண்ணை உள்ளிடவும்")
            return
        }
        val existing = allDealers.value.find { it.dealerCode.equals(dealerCode, ignoreCase = true) }
        if (existing == null) {
            onError("டீலர் ஐடி கண்டுபிடிக்கப்படவில்லை!")
            return
        }
        viewModelScope.launch {
            val updated = existing.copy(
                name = cleanName,
                shopType = shopType.trim().ifBlank { existing.shopType },
                phone = cleanPhone,
                location = cleanLoc.ifBlank { existing.location }
            )
            repository.updateDealer(updated)
            val session = currentSession.value
            if (session?.role == UserRole.DEALER && session.username.equals(dealerCode, ignoreCase = true)) {
                currentSession.value = session.copy(dealer = updated)
            }
            onSuccess()
        }
    }

    fun updateDealerFull(
        dealer: DealerEntity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (dealer.name.isBlank()) {
            onError("கடையின் பெயரை உள்ளிடவும்")
            return
        }
        if (dealer.pin.isBlank()) {
            onError("கடவுச்சொல்லை உள்ளிடவும்")
            return
        }
        viewModelScope.launch {
            repository.updateDealer(dealer)
            val session = currentSession.value
            if (session?.role == UserRole.DEALER && session.username.equals(dealer.dealerCode, ignoreCase = true)) {
                currentSession.value = session.copy(dealer = dealer)
            }
            onSuccess()
        }
    }

    fun selectDealer(code: String?) {
        selectedDealerCode.value = code
    }

    fun setStatusFilter(status: String?) {
        selectedStatusFilter.value = status
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setFormCardType(type: CardType) {
        formCardType.value = type
    }

    fun setFormPackage(pkg: AccessoriesPackage) {
        formPackage.value = pkg
    }

    fun setFormQuantity(qty: Int) {
        formQuantity.value = qty.coerceAtLeast(1)
    }

    fun submitNewOrder(onSuccess: (OrderEntity) -> Unit, onError: (String) -> Unit) {
        val name = formCustomerName.value.trim()
        val phone = formCustomerPhone.value.trim()

        if (name.isEmpty()) {
            onError("வாடிக்கையாளர் பெயரை உள்ளிடவும் (Please enter Customer Name)")
            return
        }
        if (phone.isEmpty()) {
            onError("மொபைல் எண்ணை உள்ளிடவும் (Please enter Mobile Number)")
            return
        }

        viewModelScope.launch {
            val dealerList = allDealers.value
            val session = currentSession.value
            val currentDealerCode = if (session?.role == UserRole.DEALER) {
                session.dealer?.dealerCode ?: session.username
            } else {
                selectedDealerCode.value ?: dealerList.firstOrNull()?.dealerCode ?: "DLR01"
            }
            val currentDealer = dealerList.find { it.dealerCode.equals(currentDealerCode, ignoreCase = true) }
                ?: DealerEntity(dealerCode = currentDealerCode, name = "Reya Printers", shopType = "ஜெராக்ஸ்", phone = "9842156789", location = "Madurai")

            val pricing = livePricing.value
            val orderCode = repository.generateNextOrderCode(currentDealerCode)

            val newOrder = OrderEntity(
                orderCode = orderCode,
                dealerCode = currentDealerCode,
                dealerName = currentDealer.name,
                customerName = name,
                customerPhone = phone,
                cardType = formCardType.value.name,
                accessoriesPackage = formPackage.value.name,
                quantity = pricing.quantity,
                unitDealerRate = pricing.effectiveUnitDealerPrice,
                discountPerCard = pricing.discountPerCard,
                totalDealerPayable = pricing.totalDealerPayable,
                suggestedMrpPerCard = pricing.unitSuggestedMrp,
                totalCustomerMrp = pricing.totalCustomerMrp,
                totalDealerProfit = pricing.totalDealerProfit,
                status = OrderStatus.PRINTING.code,
                notes = formNotes.value.trim(),
                stage1BatchPrinting = true, // Received & marked for batch printing
                stage2IndividualPacking = false,
                stage3MasterZiplock = false,
                stage4ShippingPacking = false,
                createdAt = System.currentTimeMillis()
            )

            val insertedId = repository.insertOrder(newOrder)
            val created = newOrder.copy(id = insertedId)

            // Reset form
            formCustomerName.value = ""
            formCustomerPhone.value = ""
            formQuantity.value = 1
            formNotes.value = ""
            formPhotoSelected.value = false
            formPaymentProofSelected.value = false

            onSuccess(created)
        }
    }

    fun updateOrderStatus(order: OrderEntity, newStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(order.id, newStatus.code)
        }
    }

    fun dispatchOrder(order: OrderEntity, courierName: String, awbNumber: String) {
        viewModelScope.launch {
            repository.dispatchOrder(
                orderId = order.id,
                newStatus = OrderStatus.DISPATCHED.code,
                courier = courierName.trim(),
                awb = awbNumber.trim()
            )
        }
    }

    fun updatePackingStages(order: OrderEntity, s1: Boolean, s2: Boolean, s3: Boolean, s4: Boolean) {
        viewModelScope.launch {
            repository.updatePackingStages(order.id, s1, s2, s3, s4)
        }
    }

    fun getOrderDetailsFileContent(order: OrderEntity): String {
        return repository.generateOrderDetailsFileContent(order)
    }

    fun generateWhatsAppMessage(order: OrderEntity, forCustomer: Boolean): String {
        val statusText = when (order.status) {
            OrderStatus.PRINTING.code -> "பிரிண்டிங் செய்யப்படுகிறது ⚙️ (Printing)"
            OrderStatus.DISPATCHED.code -> "டெலிவரிக்கு அனுப்பப்பட்டுள்ளது 🚚 (Dispatched) - கொரியர்: ${order.courierName.ifBlank { "ST Courier" }}, AWB: ${order.awbNumber.ifBlank { "உடனே வழங்கப்படும்" }}"
            OrderStatus.DELIVERED.code -> "உங்கள் கடைக்கு டெலிவரி முடிந்தது! ✓ (Delivered)"
            else -> "ஆர்டர் ஏற்றுக்கொள்ளப்பட்டது (Received)"
        }

        return if (forCustomer) {
            """
வணக்கம் ${order.customerName}! 
உங்கள் ஐடி கார்டு ஆர்டர் விவரம்:
🆔 ஆர்டர் எண்: ${order.orderCode}
🏷️ கார்டு வகை: ${if (order.cardType == "STANDARD_PVC") "ஸ்டாண்டர்ட் PVC" else "ஸ்மார்ட் RFID"}
📦 எண்ணிக்கை: ${order.quantity} கார்டுகள்
📍 தற்போதைய நிலை: $statusText

தங்கள் கார்டு பாதுகாப்பாகத் தயாரிக்கப்பட்டு வருகிறது.
🏢 REYA ID CARDS அதிகாரப்பூர்வ WhatsApp: +91 7708 910 190
நன்றி! - REYA ID CARDS (${order.dealerName})
""".trimIndent()
        } else {
            """
REYA ID CARDS டீலர் அலெர்ட்!
டீலர்: ${order.dealerName} (${order.dealerCode})
வாடிக்கையாளர்: ${order.customerName}
ஆர்டர்: ${order.orderCode}
எண்ணிக்கை: ${order.quantity} கார்டுகள்
செலுத்த வேண்டிய தொகை: ₹${"%.2f".format(order.totalDealerPayable)}
உங்கள் லாபம்: ₹${"%.2f".format(order.totalDealerProfit)}
தற்போதைய நிலை: $statusText

🏢 REYA ID CARDS அதிகாரப்பூர்வ WhatsApp: +91 7708 910 190
ஆர்டர் தொடர்பான உதவிகளுக்கு தொடர்பு கொள்ளவும்.
""".trimIndent()
        }
    }

    /**
     * Aggregates orders for the daily operations report between 6:00 AM and 8:00 PM.
     * Also provides breakdown of which dealer must receive the deliveries.
     */
    fun getDailyReport(
        selectedDateMillis: Long = System.currentTimeMillis(),
        strict6AmTo8Pm: Boolean = true
    ): DailyOperationsReport {
        val cal = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
        }

        val startCal = (cal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endCal = (cal.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val all = allOrders.value
        val dealersMap = allDealers.value.associateBy { it.dealerCode }

        val filtered = if (strict6AmTo8Pm) {
            all.filter { it.createdAt in startCal.timeInMillis..endCal.timeInMillis }
        } else {
            val dayStart = (cal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayEnd = (cal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
            all.filter { it.createdAt in dayStart..dayEnd }
        }

        // Group orders by dealer to identify delivery destinations
        val groupedByDealer = filtered.groupBy { it.dealerCode }
        val dealerDeliveries = groupedByDealer.map { (dCode, dOrders) ->
            val dealer = dealersMap[dCode]
            val totalCards = dOrders.sumOf { it.quantity }
            val hasPending = dOrders.any { it.status != "DELIVERED" }
            DealerDeliveryGroup(
                dealerCode = dCode,
                dealerName = dealer?.name ?: (dOrders.firstOrNull()?.dealerName ?: dCode),
                shopType = dealer?.shopType ?: "ஐடி கார்டு டீலர்",
                phone = dealer?.phone ?: "",
                location = dealer?.location ?: "தமிழ்நாடு",
                totalCardsToDeliver = totalCards,
                totalOrdersCount = dOrders.size,
                orders = dOrders,
                deliveryStatus = if (hasPending) "டெலிவரிக்கு தயார்" else "டெலிவரி செய்யப்பட்டது"
            )
        }.sortedByDescending { it.totalCardsToDeliver }

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date(selectedDateMillis))

        return DailyOperationsReport(
            dateString = dateStr,
            timeWindowString = if (strict6AmTo8Pm) "காலை 06:00 AM - இரவு 08:00 PM" else "முழு நாள் (All Day)",
            orders = filtered,
            dealerDeliveries = dealerDeliveries,
            totalOrdersCount = filtered.size,
            totalCardsCount = filtered.sumOf { it.quantity },
            totalPvcCards = filtered.filter { it.cardType == "STANDARD_PVC" }.sumOf { it.quantity },
            totalRfidCards = filtered.filter { it.cardType == "SMART_RFID" }.sumOf { it.quantity },
            totalValue = filtered.sumOf { it.totalDealerPayable },
            totalDealerProfit = filtered.sumOf { it.totalDealerProfit },
            pendingDispatchesCount = filtered.count { it.status != "DELIVERED" },
            completedCount = filtered.count { it.status == "DELIVERED" },
            isStrictWindow = strict6AmTo8Pm
        )
    }

    /**
     * Formats the Daily Operations Report (6 AM - 8 PM) into a comprehensive, professional WhatsApp message.
     */
    fun generateDailyReportWhatsAppMessage(report: DailyOperationsReport): String {
        val timeLabel = report.timeWindowString
        val deliveryDetails = if (report.dealerDeliveries.isEmpty()) {
            "⚠️ குறிப்பிட்ட காலக்கெடுவில் (${timeLabel}) புதிய ஆர்டர்கள் எதுவும் பதிவு செய்யப்படவில்லை."
        } else {
            report.dealerDeliveries.mapIndexed { index, d ->
                val orderLines = d.orders.joinToString("\n") { o ->
                    "   ▪️ ${o.orderCode}: ${o.quantity} கார்டுகள் (${if (o.cardType == "STANDARD_PVC") "PVC" else "RFID"}) - வாடிக்கையாளர்: ${o.customerName}"
                }
                """
${index + 1}️⃣ *டீலர்: ${d.dealerName} (#${d.dealerCode})*
   📍 டெலிவரி இடம்: ${d.location}
   📞 போன் எண்: ${d.phone.ifBlank { "பதிவு செய்யப்படவில்லை" }}
   🏬 கடை வகை: ${d.shopType}
   📦 டெலிவரி செய்ய வேண்டியவை: *${d.totalCardsToDeliver} கார்டுகள்* (${d.totalOrdersCount} ஆர்டர்கள்)
   🚚 டெலிவரி நிலை: ${d.deliveryStatus}
   📋 ஆர்டர் பட்டியல்:
$orderLines
""".trimIndent()
            }.joinToString("\n\n")
        }

        return """
📊 *REYA ID CARDS - தினசரி ஆர்டர் & டெலிவரி அறிக்கை*
📅 தேதி: ${report.dateString}
⏰ செயல்பாட்டு நேரம்: $timeLabel
━━━━━━━━━━━━━━━━━━━━━━━━━

📦 *உற்பத்தி & ஆர்டர் தொகுப்பு:*
• வந்த மொத்த ஆர்டர்கள்: ${report.totalOrdersCount}
• தயாரிக்க வேண்டிய மொத்த கார்டுகள்: ${report.totalCardsCount}
  ▫️ Standard PVC: ${report.totalPvcCards} கார்டுகள்
  ▫️ Smart RFID: ${report.totalRfidCards} கார்டுகள்
• சம்பந்தப்பட்ட டீலர்கள்: ${report.dealerDeliveries.size} பேர்
• மொத்த ஆர்டர் மதிப்பு: ₹${"%.2f".format(report.totalValue)}
• டெலிவரி நிலுவை பார்சல்கள்: ${report.pendingDispatchesCount}

━━━━━━━━━━━━━━━━━━━━━━━━━
🚚 *எந்த டீலர்க்கு டெலிவரி செய்ய வேண்டும்? (டெலிவரி அட்டவணை):*

$deliveryDetails

━━━━━━━━━━━━━━━━━━━━━━━━━
🏢 *REYA ID CARDS - மத்திய உற்பத்தி மேலாண்மை அமைப்பு*
அதிகாரப்பூர்வ உதவி: 7708 910 190
""".trimIndent()
    }
}
