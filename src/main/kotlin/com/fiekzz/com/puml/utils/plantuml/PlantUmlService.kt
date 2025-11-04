package com.fiekzz.com.puml.utils.plantuml

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO

object PlantUmlService {

    fun generateImagePng(text: String): ByteArray {

        val reader = SourceStringReader(text)

        val outputStream = ByteArrayOutputStream()

        val fileFormatOption = FileFormatOption(FileFormat.SVG)

        reader.outputImage(outputStream, fileFormatOption)
//        reader.outputImage(outputStream)

        return outputStream.toByteArray()
    }

//    fun generateImageSvg(text: String):
}