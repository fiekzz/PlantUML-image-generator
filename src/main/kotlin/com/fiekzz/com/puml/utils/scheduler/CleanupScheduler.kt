package com.fiekzz.com.puml.utils.scheduler

import com.fiekzz.com.puml.utils.plantuml.ImageService
import com.fiekzz.com.puml.utils.tracker.AccessTracker
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component

object TIMER {
    const val CLEANUP_TIMER: Long = 15 * 1000
}

@Slf4j
@Component
class CleanupScheduler(
    private val imageService: ImageService,
    private val accessTracker: AccessTracker
) {
    fun cleanup() {
        val cutOff = System.currentTimeMillis() - TIMER.CLEANUP_TIMER
        val staleIds = accessTracker.removeOlderThan(cutOff)
        staleIds.forEach { id ->
            imageService.removeImage(id)
        }
    }
}