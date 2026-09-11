package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.DealerDao
import com.example.data.dao.OrderDao
import com.example.data.entity.DealerEntity
import com.example.data.entity.OrderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [DealerEntity::class, OrderEntity::class], version = 2, exportSchema = false)
abstract class CardProDatabase : RoomDatabase() {
    abstract fun dealerDao(): DealerDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: CardProDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CardProDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CardProDatabase::class.java,
                    "card_pro_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.dealerDao(), database.orderDao())
                    }
                }
            }

            suspend fun populateInitialData(dealerDao: DealerDao, orderDao: OrderDao) {
                // Pre-populate dealers matching SOP document Section 1 & Section 4
                val dealers = listOf(
                    DealerEntity(
                        dealerCode = "DLR01",
                        name = "Reya Printers",
                        shopType = "ஜெராக்ஸ் & பிரிண்டிங் சென்டர்",
                        phone = "9842156789",
                        location = "Madurai Town",
                        upiId = "reyaprinters@okhdfcbank",
                        starterKitDelivered = true
                    ),
                    DealerEntity(
                        dealerCode = "DLR02",
                        name = "Sai Sevai Maiyam",
                        shopType = "இ-சேவை மையம் (E-Sevai)",
                        phone = "9789123450",
                        location = "Tirunelveli Junction",
                        upiId = "saisevai@okaxis",
                        starterKitDelivered = true
                    ),
                    DealerEntity(
                        dealerCode = "DLR03",
                        name = "Star Photo Studio",
                        shopType = "பாஸ்போர்ட் போட்டோ ஸ்டுடியோ",
                        phone = "9443218765",
                        location = "Dindigul Main Road",
                        upiId = "starstudio@upi",
                        starterKitDelivered = true
                    ),
                    DealerEntity(
                        dealerCode = "DLR04",
                        name = "Sri Murugan Stationery",
                        shopType = "ஸ்டேஷனரி & ஸ்கூல் சப்ளைஸ்",
                        phone = "9150067890",
                        location = "Coimbatore North",
                        upiId = "muruganstat@upi",
                        starterKitDelivered = false
                    )
                )
                dealerDao.insertAll(dealers)

                // Pre-populate initial orders matching SOP Section 4 & 5
                val initialOrders = listOf(
                    OrderEntity(
                        orderCode = "DLR01-09SEP-001",
                        dealerCode = "DLR01",
                        dealerName = "Reya Printers",
                        customerName = "Karthik Raja",
                        customerPhone = "9843210987",
                        cardType = "STANDARD_PVC",
                        accessoriesPackage = "CARD_CASE_MULTICOLOR_LANYARD",
                        quantity = 25,
                        unitDealerRate = 82.0, // 85 - 3 discount
                        discountPerCard = 3.0,
                        totalDealerPayable = 2050.0,
                        suggestedMrpPerCard = 160.0,
                        totalCustomerMrp = 4000.0,
                        totalDealerProfit = 1950.0,
                        status = "PRINTING",
                        courierName = "Shiprocket",
                        awbNumber = "SR987452310IN",
                        notes = "St. Joseph Matriculation School Teacher IDs",
                        photoUri = "",
                        paymentProofUri = "",
                        stage1BatchPrinting = true,
                        stage2IndividualPacking = false,
                        stage3MasterZiplock = false,
                        stage4ShippingPacking = false,
                        createdAt = System.currentTimeMillis() - 86400000L
                    ),
                    OrderEntity(
                        orderCode = "DLR02-09SEP-002",
                        dealerCode = "DLR02",
                        dealerName = "Sai Sevai Maiyam",
                        customerName = "Suresh Kumar",
                        customerPhone = "9790123456",
                        cardType = "SMART_RFID",
                        accessoriesPackage = "CARD_CASE_PLAIN_LANYARD",
                        quantity = 15,
                        unitDealerRate = 87.0, // 90 - 3 discount
                        discountPerCard = 3.0,
                        totalDealerPayable = 1305.0,
                        suggestedMrpPerCard = 170.0,
                        totalCustomerMrp = 2550.0,
                        totalDealerProfit = 1245.0,
                        status = "DISPATCHED",
                        courierName = "ST Courier",
                        awbNumber = "ST66209418TN",
                        notes = "Factory RFID Access Cards",
                        photoUri = "",
                        paymentProofUri = "",
                        stage1BatchPrinting = true,
                        stage2IndividualPacking = true,
                        stage3MasterZiplock = true,
                        stage4ShippingPacking = true,
                        createdAt = System.currentTimeMillis() - 43200000L
                    ),
                    OrderEntity(
                        orderCode = "DLR01-10SEP-003",
                        dealerCode = "DLR01",
                        dealerName = "Reya Printers",
                        customerName = "Priya Dharshini",
                        customerPhone = "9442198765",
                        cardType = "STANDARD_PVC",
                        accessoriesPackage = "CARD_CASE",
                        quantity = 2,
                        unitDealerRate = 40.0,
                        discountPerCard = 0.0,
                        totalDealerPayable = 80.0,
                        suggestedMrpPerCard = 80.0,
                        totalCustomerMrp = 160.0,
                        totalDealerProfit = 80.0,
                        status = "DELIVERED",
                        courierName = "Bus Parcel (ABT)",
                        awbNumber = "ABT-MDU-789",
                        notes = "Urgent Driver License Plastic ID",
                        photoUri = "",
                        paymentProofUri = "",
                        stage1BatchPrinting = true,
                        stage2IndividualPacking = true,
                        stage3MasterZiplock = true,
                        stage4ShippingPacking = true,
                        createdAt = System.currentTimeMillis() - 172800000L
                    )
                )
                orderDao.insertAll(initialOrders)
            }
        }
    }
}
