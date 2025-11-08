package com.fiekzz.com.puml.utils.lifecycle

import com.fiekzz.com.puml.utils.cache.UMLCache
import com.fiekzz.com.puml.utils.debug.logger
import com.fiekzz.com.puml.utils.plantuml.ImageService
import com.fiekzz.com.puml.utils.scheduler.CleanupScheduler
import com.fiekzz.com.puml.utils.scheduler.TIMER
import com.fiekzz.com.puml.utils.tracker.AccessTracker
import jakarta.annotation.PreDestroy
import lombok.extern.slf4j.Slf4j
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Slf4j
class AppLifeCycle(
    private val imageService: ImageService,
    private val umlCache: UMLCache,
    private val cleanupScheduler: CleanupScheduler
) {

    @PreDestroy
    fun preDestroy() {
        imageService.removeAllImages()
        umlCache.removeAll()
        logger<AppLifeCycle>().info("AppLifeCycle preDestroy")
    }

    @Scheduled(fixedRate = TIMER.CLEANUP_TIMER)
    fun cleanupScheduler() {
        cleanupScheduler.cleanup()
    }
}