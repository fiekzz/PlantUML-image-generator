package com.fiekzz.com.puml.utils.plantuml

import com.fiekzz.com.puml.utils.cache.UMLCache
import com.fiekzz.com.puml.utils.debug.logger
import com.fiekzz.com.puml.utils.lifecycle.AppLifeCycle
import com.fiekzz.com.puml.utils.tracker.AccessTracker
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import java.io.File

@Slf4j
@Service
class ImageService(
    private val umlCache: UMLCache,
    private val accessTracker: AccessTracker
) {
    private val tempDir = File(System.getProperty("java.io.tmpdir"), "plantuml-images").apply {
        mkdirs()
    }

    fun generateImage(source: String): String {
        val id = hash(source)
        val file = File(tempDir, "${id}.png")

        if (!file.exists()) {
            val imageBytes = PlantUmlService.generateImage(source)
            file.writeBytes(imageBytes)
            umlCache.put(id, imageBytes)
        }

        accessTracker.touch(id)
        return id
    }

    fun getImage(id: String): ByteArray? {
        val cached = umlCache.get(id)
        logger<ImageService>().info(cached.toString())
        if (cached != null) {
            accessTracker.touch(id)
            return cached
        }

        val file = File(tempDir, "${id}.png")
        if (file.exists()) {
            accessTracker.touch(id)
            val imageFile = file.readBytes().also { umlCache.put(id, it) }
            return imageFile
        }
        return null
    }

    private fun hash(text: String): String {
        return com.google.common.hash.Hashing.sha256()
            .hashString(text, Charsets.UTF_8)
            .toString()
            .substring(0, 16)
    }

    fun removeImage(id: String) {
        val file = File(tempDir, "${id}.png")
        if (file.exists()) {
            file.deleteRecursively()
            umlCache.evict(id)
            logger<ImageService>().info("File deleted $file")
        }
    }

    fun removeAllImages() {
        tempDir.deleteRecursively()
    }
}