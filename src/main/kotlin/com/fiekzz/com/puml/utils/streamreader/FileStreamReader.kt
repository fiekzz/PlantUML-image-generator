package com.fiekzz.com.puml.utils.streamreader

import java.io.InputStream

object FileStreamReader {
    fun readInputStreamContent(inputStream: InputStream): String {
        inputStream.bufferedReader().use { it ->
            return it.readText()
        }
    }
}