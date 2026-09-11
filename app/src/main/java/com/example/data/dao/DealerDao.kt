package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.DealerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DealerDao {
    @Query("SELECT * FROM dealers ORDER BY dealerCode ASC")
    fun getAllDealers(): Flow<List<DealerEntity>>

    @Query("SELECT * FROM dealers WHERE dealerCode = :dealerCode LIMIT 1")
    suspend fun getDealerByCode(dealerCode: String): DealerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDealer(dealer: DealerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dealers: List<DealerEntity>)

    @Update
    suspend fun updateDealer(dealer: DealerEntity)

    @Query("SELECT COUNT(*) FROM dealers")
    suspend fun getDealerCount(): Int
}
