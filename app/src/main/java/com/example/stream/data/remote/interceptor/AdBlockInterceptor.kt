package com.example.stream.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Ad blocking interceptor that filters requests to known ad and tracking domains
 * Similar to AdGuard DNS filtering but at the application level
 */
class AdBlockInterceptor : Interceptor {
    
    // Common ad and tracking domains to block
    private val blockedDomains = setOf(
        // Ad networks
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "google-analytics.com",
        "googletagmanager.com",
        "googletagservices.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        
        // Analytics and tracking
        "analytics.google.com",
        "stats.g.doubleclick.net",
        "facebook.com/tr",
        "connect.facebook.net",
        "pixel.facebook.com",
        
        // Common ad servers
        "ads.yahoo.com",
        "advertising.com",
        "adnxs.com",
        "adsrvr.org",
        "criteo.com",
        "outbrain.com",
        "taboola.com",
        "pubmatic.com",
        "rubiconproject.com",
        
        // Tracking pixels
        "scorecardresearch.com",
        "quantserve.com",
        "hotjar.com",
        "mouseflow.com",
        
        // Video ad networks
        "imasdk.googleapis.com",
        "video-ad-stats.googlesyndication.com",
        "pubads.g.doubleclick.net"
    )
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val host = request.url.host
        
        // Check if the domain should be blocked
        if (shouldBlockDomain(host)) {
            // Return a blocked response
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(403)
                .message("Blocked by AdBlock")
                .body("".toResponseBody(null))
                .build()
        }
        
        // Proceed with the request if not blocked
        return chain.proceed(request)
    }
    
    /**
     * Check if a domain should be blocked
     */
    private fun shouldBlockDomain(host: String): Boolean {
        // Check exact match
        if (blockedDomains.contains(host)) {
            return true
        }
        
        // Check if host ends with any blocked domain (for subdomains)
        return blockedDomains.any { blockedDomain ->
            host.endsWith(".$blockedDomain") || host == blockedDomain
        }
    }
    
    /**
     * Add a domain to the block list
     */
    fun addBlockedDomain(domain: String) {
        (blockedDomains as? MutableSet)?.add(domain)
    }
    
    /**
     * Remove a domain from the block list
     */
    fun removeBlockedDomain(domain: String) {
        (blockedDomains as? MutableSet)?.remove(domain)
    }
}
