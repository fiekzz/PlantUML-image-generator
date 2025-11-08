package com.fiekzz.com.puml.utils.cache

import com.github.benmanes.caffeine.cache.Caffeine
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Component
@Slf4j
class UMLCache {
    private val cache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterAccess(1, TimeUnit.HOURS)
        .build<String, ByteArray>()

    fun get(id: String): ByteArray? = cache.getIfPresent(id)
    fun put(id: String, data: ByteArray) = cache.put(id, data)
    fun evict(id: String) = cache.invalidate(id)
    fun removeAll() {
        cache.invalidateAll()
    }
}