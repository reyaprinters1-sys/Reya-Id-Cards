package com.example.model

enum class CardType(val id: String, val titleTa: String, val titleEn: String) {
    STANDARD_PVC("STANDARD_PVC", "ஸ்டாண்டர்ட் PVC ஐடி கார்டு", "Standard PVC Card"),
    SMART_RFID("SMART_RFID", "ஸ்மார்ட் / RFID கார்டு", "Smart RFID Card")
}

enum class AccessoriesPackage(
    val id: String,
    val titleTa: String,
    val titleEn: String,
    val subtitleTa: String,
    val pvcCost: Double,
    val pvcDealerPrice: Double,
    val pvcMrp: Double,
    val rfidCost: Double,
    val rfidDealerPrice: Double,
    val rfidMrp: Double
) {
    ONLY_CARD(
        id = "ONLY_CARD",
        titleTa = "Only Card (இருபுற வண்ணப் பிரிண்டிங்)",
        titleEn = "Only Card (Dual Sided Color Print)",
        subtitleTa = "கார்டு மட்டும்",
        pvcCost = 15.0,
        pvcDealerPrice = 30.0,
        pvcMrp = 60.0,
        rfidCost = 35.0,
        rfidDealerPrice = 65.0,
        rfidMrp = 120.0
    ),
    CARD_CASE(
        id = "CARD_CASE",
        titleTa = "Card + Transparent Case",
        titleEn = "Card + Transparent Case (Pouch / Holder)",
        subtitleTa = "பவுச் / ஹோல்டர் சேர்க்கை",
        pvcCost = 20.0,
        pvcDealerPrice = 40.0,
        pvcMrp = 80.0,
        rfidCost = 40.0,
        rfidDealerPrice = 75.0,
        rfidMrp = 140.0
    ),
    CARD_CASE_PLAIN_LANYARD(
        id = "CARD_CASE_PLAIN_LANYARD",
        titleTa = "Card + Case + Plain Lanyard",
        titleEn = "Card + Case + Plain Lanyard",
        subtitleTa = "சாதாரண கயிறு சேர்க்கை",
        pvcCost = 26.0,
        pvcDealerPrice = 55.0,
        pvcMrp = 110.0,
        rfidCost = 46.0,
        rfidDealerPrice = 90.0,
        rfidMrp = 170.0
    ),
    CARD_CASE_MULTICOLOR_LANYARD(
        id = "CARD_CASE_MULTICOLOR_LANYARD",
        titleTa = "Card + Case + Multi-color Lanyard",
        titleEn = "Card + Case + Multi-color Lanyard",
        subtitleTa = "பிராண்டட் கலர் கயிறு சேர்க்கை",
        pvcCost = 48.0,
        pvcDealerPrice = 85.0,
        pvcMrp = 160.0,
        rfidCost = 68.0,
        rfidDealerPrice = 125.0,
        rfidMrp = 230.0
    );

    fun getUnitDealerPrice(cardType: CardType): Double =
        if (cardType == CardType.STANDARD_PVC) pvcDealerPrice else rfidDealerPrice

    fun getUnitMrp(cardType: CardType): Double =
        if (cardType == CardType.STANDARD_PVC) pvcMrp else rfidMrp

    fun getUnitCost(cardType: CardType): Double =
        if (cardType == CardType.STANDARD_PVC) pvcCost else rfidCost

    fun getUnitDealerProfit(cardType: CardType): Double =
        getUnitMrp(cardType) - getUnitDealerPrice(cardType)
}

data class RateCardConfig(
    // Package 1: Only Card
    val onlyCardPvcDealer: Double = 30.0,
    val onlyCardPvcMrp: Double = 60.0,
    val onlyCardRfidDealer: Double = 65.0,
    val onlyCardRfidMrp: Double = 120.0,

    // Package 2: Card + Case
    val cardCasePvcDealer: Double = 40.0,
    val cardCasePvcMrp: Double = 80.0,
    val cardCaseRfidDealer: Double = 75.0,
    val cardCaseRfidMrp: Double = 140.0,

    // Package 3: Card + Case + Plain Lanyard
    val plainLanyardPvcDealer: Double = 55.0,
    val plainLanyardPvcMrp: Double = 110.0,
    val plainLanyardRfidDealer: Double = 90.0,
    val plainLanyardRfidMrp: Double = 170.0,

    // Package 4: Card + Case + Multi-color Lanyard
    val multiLanyardPvcDealer: Double = 85.0,
    val multiLanyardPvcMrp: Double = 160.0,
    val multiLanyardRfidDealer: Double = 125.0,
    val multiLanyardRfidMrp: Double = 230.0,

    // Bulk discount slabs
    val discount11to50: Double = 3.0,
    val discount51to200: Double = 5.0,
    val discount200Plus: Double = 8.0
) {
    fun getUnitDealerPrice(pkg: AccessoriesPackage, cardType: CardType): Double = when (pkg) {
        AccessoriesPackage.ONLY_CARD -> if (cardType == CardType.STANDARD_PVC) onlyCardPvcDealer else onlyCardRfidDealer
        AccessoriesPackage.CARD_CASE -> if (cardType == CardType.STANDARD_PVC) cardCasePvcDealer else cardCaseRfidDealer
        AccessoriesPackage.CARD_CASE_PLAIN_LANYARD -> if (cardType == CardType.STANDARD_PVC) plainLanyardPvcDealer else plainLanyardRfidDealer
        AccessoriesPackage.CARD_CASE_MULTICOLOR_LANYARD -> if (cardType == CardType.STANDARD_PVC) multiLanyardPvcDealer else multiLanyardRfidDealer
    }

    fun getUnitMrp(pkg: AccessoriesPackage, cardType: CardType): Double = when (pkg) {
        AccessoriesPackage.ONLY_CARD -> if (cardType == CardType.STANDARD_PVC) onlyCardPvcMrp else onlyCardRfidMrp
        AccessoriesPackage.CARD_CASE -> if (cardType == CardType.STANDARD_PVC) cardCasePvcMrp else cardCaseRfidMrp
        AccessoriesPackage.CARD_CASE_PLAIN_LANYARD -> if (cardType == CardType.STANDARD_PVC) plainLanyardPvcMrp else plainLanyardRfidMrp
        AccessoriesPackage.CARD_CASE_MULTICOLOR_LANYARD -> if (cardType == CardType.STANDARD_PVC) multiLanyardPvcMrp else multiLanyardRfidMrp
    }

    fun getUnitDealerProfit(pkg: AccessoriesPackage, cardType: CardType): Double =
        getUnitMrp(pkg, cardType) - getUnitDealerPrice(pkg, cardType)
}

data class PricingCalculation(
    val cardType: CardType,
    val accessoriesPackage: AccessoriesPackage,
    val quantity: Int,
    val unitBaseDealerPrice: Double,
    val discountPerCard: Double,
    val effectiveUnitDealerPrice: Double,
    val totalDealerPayable: Double,
    val unitSuggestedMrp: Double,
    val totalCustomerMrp: Double,
    val totalDealerProfit: Double,
    val discountSlabDescription: String
)

object PricingCalculator {
    fun calculate(
        cardType: CardType,
        accessoriesPackage: AccessoriesPackage,
        quantity: Int,
        config: RateCardConfig = RateCardConfig()
    ): PricingCalculation {
        val qty = if (quantity <= 0) 1 else quantity
        val baseDealerPrice = config.getUnitDealerPrice(accessoriesPackage, cardType)
        val unitMrp = config.getUnitMrp(accessoriesPackage, cardType)

        // Dynamic bulk discount slabs
        val (discount, slabDesc) = when {
            qty in 1..10 -> 0.0 to "1–10 கார்டுகள்: வழக்கமான டீலர் விலை"
            qty in 11..50 -> config.discount11to50 to "11–50 கார்டுகள்: ₹${"%.0f".format(config.discount11to50)} தள்ளுபடி / கார்டு"
            qty in 51..200 -> config.discount51to200 to "51–200 கார்டுகள்: ₹${"%.0f".format(config.discount51to200)} தள்ளுபடி / கார்டு"
            else -> config.discount200Plus to "200+ கார்டுகள்: ₹${"%.0f".format(config.discount200Plus)} மெகா தள்ளுபடி / கார்டு"
        }

        val effectiveDealerRate = (baseDealerPrice - discount).coerceAtLeast(1.0)
        val totalDealerPayable = effectiveDealerRate * qty
        val totalCustomerMrp = unitMrp * qty
        val totalDealerProfit = totalCustomerMrp - totalDealerPayable

        return PricingCalculation(
            cardType = cardType,
            accessoriesPackage = accessoriesPackage,
            quantity = qty,
            unitBaseDealerPrice = baseDealerPrice,
            discountPerCard = discount,
            effectiveUnitDealerPrice = effectiveDealerRate,
            totalDealerPayable = totalDealerPayable,
            unitSuggestedMrp = unitMrp,
            totalCustomerMrp = totalCustomerMrp,
            totalDealerProfit = totalDealerProfit,
            discountSlabDescription = slabDesc
        )
    }
}
