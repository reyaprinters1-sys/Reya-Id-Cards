package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AccessoriesPackage
import com.example.model.CardType
import com.example.model.PricingCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun readStringFromContext() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Reya ID Cards", appName)
  }

  @Test
  fun testSopRateCardCalculationsStandardPvc() {
    // SOP Section 2.A: Standard PVC Only Card: Dealer ₹30, MRP ₹60, Profit ₹30
    val result1 = PricingCalculator.calculate(CardType.STANDARD_PVC, AccessoriesPackage.ONLY_CARD, 1)
    assertEquals(30.0, result1.unitBaseDealerPrice, 0.01)
    assertEquals(30.0, result1.totalDealerPayable, 0.01)
    assertEquals(60.0, result1.totalCustomerMrp, 0.01)
    assertEquals(30.0, result1.totalDealerProfit, 0.01)

    // SOP Section 2.A: Card + Case + Multi-color Lanyard: Dealer ₹85, MRP ₹160
    val result4 = PricingCalculator.calculate(CardType.STANDARD_PVC, AccessoriesPackage.CARD_CASE_MULTICOLOR_LANYARD, 1)
    assertEquals(85.0, result4.unitBaseDealerPrice, 0.01)
    assertEquals(160.0, result4.totalCustomerMrp, 0.01)
    assertEquals(75.0, result4.totalDealerProfit, 0.01)
  }

  @Test
  fun testSopBulkSlabsDiscount() {
    // 1-10 cards: No discount
    val res10 = PricingCalculator.calculate(CardType.STANDARD_PVC, AccessoriesPackage.ONLY_CARD, 10)
    assertEquals(0.0, res10.discountPerCard, 0.01)
    assertEquals(300.0, res10.totalDealerPayable, 0.01)

    // 11-50 cards: ₹3 discount per card
    val res25 = PricingCalculator.calculate(CardType.STANDARD_PVC, AccessoriesPackage.ONLY_CARD, 25)
    assertEquals(3.0, res25.discountPerCard, 0.01)
    assertEquals(27.0, res25.effectiveUnitDealerPrice, 0.01)
    assertEquals(675.0, res25.totalDealerPayable, 0.01)

    // 51-200 cards: ₹5 discount per card
    val res100 = PricingCalculator.calculate(CardType.STANDARD_PVC, AccessoriesPackage.ONLY_CARD, 100)
    assertEquals(5.0, res100.discountPerCard, 0.01)
    assertEquals(25.0, res100.effectiveUnitDealerPrice, 0.01)
    assertEquals(2500.0, res100.totalDealerPayable, 0.01)
  }

  @Test
  fun testSopSmartRfidPricing() {
    // SOP Section 2.B: RFID Card + Case + Multi-color Lanyard: Cost ₹68, Dealer ₹125, MRP ₹230, Profit ₹105
    val resRfid = PricingCalculator.calculate(CardType.SMART_RFID, AccessoriesPackage.CARD_CASE_MULTICOLOR_LANYARD, 1)
    assertEquals(125.0, resRfid.unitBaseDealerPrice, 0.01)
    assertEquals(230.0, resRfid.unitSuggestedMrp, 0.01)
    assertEquals(105.0, resRfid.totalDealerProfit, 0.01)
  }
}
