package com.fiekzz.com.puml.utils.configurations

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

class PlantUMLRuntimeHints : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {

        // Register AWT classes needed by PlantUML
        val awtClasses = listOf(
            "java.awt.Font",
            "java.awt.Color",
            "java.awt.Graphics2D",
            "java.awt.image.BufferedImage",
            "java.awt.geom.Rectangle2D",
            "java.awt.geom.Rectangle2D\$Double",
            "java.awt.geom.Dimension2D",
            "java.awt.font.FontRenderContext",
            "sun.font.SunFontManager",
            "sun.java2d.SunGraphics2D"
        )

        awtClasses.forEach { className ->
            try {
                val clazz = Class.forName(className)
                hints.reflection().registerType(
                    clazz,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.DECLARED_FIELDS
                )
            } catch (e: ClassNotFoundException) {
                // Class doesn't exist in this JDK version, skip
            }
        }

        // Register PlantUML classes that use reflection
        val plantUMLClasses = listOf(
            "net.sourceforge.plantuml.SourceStringReader",
            "net.sourceforge.plantuml.FileFormat",
            "net.sourceforge.plantuml.FileFormatOption",
            "net.sourceforge.plantuml.klimt.color.HColor",
            "net.sourceforge.plantuml.klimt.drawing.svg.SvgGraphics"
        )

        plantUMLClasses.forEach { className ->
            try {
                val clazz = Class.forName(className)
                hints.reflection().registerType(
                    clazz,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.DECLARED_FIELDS
                )
            } catch (e: ClassNotFoundException) {
                // Class doesn't exist, skip
            }
        }

        // Register Caffeine cache classes
        val caffeineClasses = listOf(
            "com.github.benmanes.caffeine.cache.SSMS",
            "com.github.benmanes.caffeine.cache.PSMS",
            "com.github.benmanes.caffeine.cache.BoundedLocalCache",
            "com.github.benmanes.caffeine.cache.UnboundedLocalCache"
        )

        caffeineClasses.forEach { className ->
            try {
                val clazz = Class.forName(className)
                hints.reflection().registerType(
                    clazz,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.DECLARED_FIELDS
                )
            } catch (e: ClassNotFoundException) {
                // Class doesn't exist, skip
            }
        }

        // Register resources needed by PlantUML
        hints.resources().registerPattern("*.png")
        hints.resources().registerPattern("*.svg")
        hints.resources().registerPattern("*.txt")
        hints.resources().registerPattern("*.ttf")
        hints.resources().registerPattern("sprites/*")
    }
}