package com.fiekzz.com.puml.utils.plantuml

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO

object UmlOutput {
    enum class UmlOutputType(val value: FileFormat) {
        SVG(FileFormat.SVG),
        PNG(FileFormat.PNG);

        fun getContentType(): String = when(this) {
            SVG -> "image/svg+xml"
            PNG -> "image/png"
        }
    }

    fun tryFindOutputByName(name: String): UmlOutputType? {
        return try {
            UmlOutputType.valueOf(name.uppercase())
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun findOutputByName(name: String): UmlOutputType {
        return UmlOutputType.valueOf(name.uppercase())
    }
}

object PlantUmlService {

    fun generateImage(text: String, outputFormat: FileFormat = FileFormat.PNG): ByteArray {

        val reader = SourceStringReader(text)

        val outputStream = ByteArrayOutputStream()

        val fileFormatOption = FileFormatOption(outputFormat)

        reader.outputImage(outputStream, fileFormatOption)

        return outputStream.toByteArray()
    }
}