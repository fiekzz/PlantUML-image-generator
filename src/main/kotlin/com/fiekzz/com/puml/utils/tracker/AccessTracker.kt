package com.fiekzz.com.puml.utils.tracker

import com.fiekzz.com.puml.utils.debug.logger
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
@Slf4j
class AccessTracker {
    private val lastAccess = ConcurrentHashMap<String, Long>()

    fun touch(id: String) {
        lastAccess[id] = System.currentTimeMillis()
    }

    fun removeOlderThan(cutoff: Long): Set<String> {
        return lastAccess.entries
            .filter { it.value < cutoff }
            .map { it.key }
            .onEach { lastAccess.remove(it) }
            .toSet()
    }

    fun getItemsOlderThan(cutoff: Long): Set<String> {
        return lastAccess.entries
            .filter { it.value < cutoff }
            .map { it.key }
            .toSet()
    }
}