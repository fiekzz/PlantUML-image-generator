package com.fiekzz.com.puml.utils.cache

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class UMLCache {
    private val cache = ConcurrentHashMap<String, CacheEntry>()

    data class CacheEntry(val data: ByteArray, val timestamp: Long = System.currentTimeMillis())

    fun get(id: String): ByteArray? {
        val entry = cache[id] ?: return null
        // Simple expiration check (1 hour)
        return if (System.currentTimeMillis() - entry.timestamp < 3600000) {
            entry.data
        } else {
            cache.remove(id)
            null
        }
    }

    fun put(id: String, data: ByteArray) {
        cache[id] = CacheEntry(data)
        // Simple size limit
        if (cache.size > 10_000) {
            cache.entries.firstOrNull()?.let { cache.remove(it.key) }
        }
    }

    fun evict(id: String) = cache.remove(id)

    fun removeAll() = cache.clear()
}