package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkDao {

    // --- Trusted Networks ---
    @Query("SELECT * FROM trusted_networks ORDER BY addedAt DESC")
    fun getAllTrustedNetworks(): Flow<List<TrustedNetwork>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrustedNetwork(network: TrustedNetwork)

    @Query("DELETE FROM trusted_networks WHERE id = :id")
    suspend fun deleteTrustedNetworkById(id: Int)

    @Query("DELETE FROM trusted_networks WHERE ssid = :ssid")
    suspend fun deleteTrustedNetworkBySsid(ssid: String)

    @Query("SELECT EXISTS(SELECT 1 FROM trusted_networks WHERE ssid = :ssid LIMIT 1)")
    suspend fun isNetworkTrusted(ssid: String): Boolean


    // --- Network History ---
    @Query("SELECT * FROM network_history ORDER BY timestamp DESC")
    fun getNetworkHistory(): Flow<List<NetworkHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: NetworkHistory)

    @Query("DELETE FROM network_history")
    suspend fun clearHistory()
}
