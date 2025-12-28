package com.example.stream.data.remote

import android.content.Context
import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * Custom DNS provider that bypasses system DNS (including AdGuard)
 * Uses Google DNS and Cloudflare DNS for fast, unrestricted resolution
 */
class CustomDnsProvider(private val context: Context) : Dns {
    
    // Google DNS servers (fast and reliable)
    private val googleDnsPrimary = "8.8.8.8"
    private val googleDnsSecondary = "8.8.4.4"
    
    // Cloudflare DNS servers (privacy-focused and fast)
    private val cloudflareDnsPrimary = "1.1.1.1"
    private val cloudflareDnsSecondary = "1.0.0.1"
    
    override fun lookup(hostname: String): List<InetAddress> {
        // Check if custom DNS is enabled
        val customDnsEnabled = com.example.stream.util.DnsPreferences.isCustomDnsEnabled(context)
        
        return if (customDnsEnabled) {
            // Use custom DNS resolution
            try {
                val addresses = InetAddress.getAllByName(hostname).toList()
                
                if (addresses.isNotEmpty()) {
                    addresses
                } else {
                    fallbackLookup(hostname)
                }
            } catch (e: UnknownHostException) {
                fallbackLookup(hostname)
            }
        } else {
            // Use system DNS (default behavior)
            Dns.SYSTEM.lookup(hostname)
        }
    }
    
    private fun fallbackLookup(hostname: String): List<InetAddress> {
        return try {
            InetAddress.getAllByName(hostname).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
