package com.example.stream.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager for DNS preferences
 */
object DnsPreferences {
    private const val PREFS_NAME = "dns_preferences"
    private const val KEY_CUSTOM_DNS_ENABLED = "custom_dns_enabled"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun isCustomDnsEnabled(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_CUSTOM_DNS_ENABLED, true) // Default: enabled
    }
    
    fun setCustomDnsEnabled(context: Context, enabled: Boolean) {
        getPreferences(context).edit()
            .putBoolean(KEY_CUSTOM_DNS_ENABLED, enabled)
            .apply()
    }
}
