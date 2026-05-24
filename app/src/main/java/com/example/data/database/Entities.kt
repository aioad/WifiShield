package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trusted_networks")
data class TrustedNetwork(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ssid: String,
    val type: String, // Home, Office, College, Custom
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "network_history")
data class NetworkHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ssid: String,
    val securityStatus: String, // SAFE, RISKY, UNSAFE
    val securityScore: Int,
    val encryptionType: String,
    val vpnUsed: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
