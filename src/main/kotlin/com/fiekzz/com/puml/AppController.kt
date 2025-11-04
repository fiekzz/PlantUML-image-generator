package com.fiekzz.com.puml

import com.fiekzz.com.puml.model.apiresponse.SuccessResponse
import com.fiekzz.com.puml.utils.contenttype.AppContentType
import com.fiekzz.com.puml.utils.debug.logger
import com.fiekzz.com.puml.utils.plantuml.PlantUmlService
import com.fiekzz.com.puml.utils.route.APIROUTES
import com.fiekzz.com.puml.utils.streamreader.FileStreamReader
import com.fiekzz.com.puml.utils.validator.ValidFile
import lombok.extern.slf4j.Slf4j
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Slf4j
@RestController
class AppController {

    @GetMapping(APIROUTES.DEFAULT.BASE, APIROUTES.DEFAULT.GET_BASE)
    fun getRoot(): ResponseEntity<SuccessResponse<Nothing>> {
        return ResponseEntity.ok(
            SuccessResponse(
                message = "PlantUML Hello",
                success = true,
                data = null
            )
        )
    }

    @GetMapping(APIROUTES.DEFAULT.GET_HEALTH)
    fun getHealth() : ResponseEntity<SuccessResponse<Nothing>> {
        return ResponseEntity.ok(
            SuccessResponse(
                message = "Server is in great condition",
                success = true,
                data = null
            )
        )
    }

    @PostMapping(
        APIROUTES.PLANTUML.POST_FILE_GENERATE_UML,
        consumes = [AppContentType.MULTIPART_FORM]
    )
    fun postGeneratePlantUML(
        @RequestParam("file")
        @ValidFile()
        file: MultipartFile,
    ) : ResponseEntity<ByteArray> {

        val text = FileStreamReader.readInputStreamContent(file.inputStream)

        val file = PlantUmlService.generateImagePng(text)

        val headers = HttpHeaders()

        headers.add("Content-Type", "image/svg+xml")

        return ResponseEntity.ok().headers(headers).body(file)
    }

    @PostMapping(
        APIROUTES.PLANTUML.POST_TEXT_GENERATE_UML,
        consumes = [AppContentType.TEXT]
    )
    fun postGeneratePlantUMLText(
        @RequestBody text: String,
    ): ResponseEntity<ByteArray> {
        val file = PlantUmlService.generateImagePng(text)

        val headers = HttpHeaders()

        headers.add("Content-Type", "image/svg+xml")

        return ResponseEntity.ok().headers(headers).body(file)
    }
}