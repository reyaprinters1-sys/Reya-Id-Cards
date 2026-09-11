package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE dealerCode = :dealerCode ORDER BY createdAt DESC")
    fun getOrdersByDealer(dealerCode: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Query("SELECT * FROM orders WHERE orderCode = :orderCode LIMIT 1")
    suspend fun getOrderByCode(orderCode: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<OrderEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :newStatus WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, newStatus: String)

    @Query("UPDATE orders SET status = :newStatus, courierName = :courier, awbNumber = :awb WHERE id = :orderId")
    suspend fun dispatchOrder(orderId: Long, newStatus: String, courier: String, awb: String)

    @Query("UPDATE orders SET stage1BatchPrinting = :s1, stage2IndividualPacking = :s2, stage3MasterZiplock = :s3, stage4ShippingPacking = :s4 WHERE id = :orderId")
    suspend fun updatePackingStages(orderId: Long, s1: Boolean, s2: Boolean, s3: Boolean, s4: Boolean)

    @Query("SELECT COUNT(*) FROM orders WHERE orderCode LIKE :prefix || '%'")
    suspend fun countOrdersWithPrefix(prefix: String): Int

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getTotalOrderCount(): Int
}
