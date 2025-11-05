package com.fiekzz.com.puml

import com.fiekzz.com.puml.model.apiresponse.SuccessResponse
import com.fiekzz.com.puml.model.plantuml.PlantUMLTextRequest
import com.fiekzz.com.puml.utils.contenttype.AppContentType
import com.fiekzz.com.puml.utils.debug.logger
import com.fiekzz.com.puml.utils.plantuml.PlantUmlService
import com.fiekzz.com.puml.utils.plantuml.UmlOutput
import com.fiekzz.com.puml.utils.route.APIROUTES
import com.fiekzz.com.puml.utils.streamreader.FileStreamReader
import com.fiekzz.com.puml.utils.validator.ValidFile
import com.fiekzz.com.puml.utils.validator.ValidUMLText
import lombok.extern.slf4j.Slf4j
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid

@Slf4j
@RestController
//@RequestMapping
class AppController {

    companion object {
        val AppLog = logger<AppController>()
    }

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
        @RequestParam("outputType", required = false)
        @ValidUMLText()
        type: String,
    ) : ResponseEntity<ByteArray> {
        val text = FileStreamReader.readInputStreamContent(file.inputStream)
        return generateUmlResponse(text, type)
    }

    @PostMapping(
        APIROUTES.PLANTUML.POST_TEXT_GENERATE_UML,
        consumes = [AppContentType.JSON]
    )
    fun postGeneratePlantUMLText(
        @Valid
        @RequestBody request: PlantUMLTextRequest
    ): ResponseEntity<ByteArray> {
        AppLog.info(request.text, request.outputType)
        return generateUmlResponse(request.text, request.outputType)
    }

    private fun generateUmlResponse(text: String, type: String): ResponseEntity<ByteArray> {
        val outputType = UmlOutput.findOutputByName(type)
        val file = PlantUmlService.generateImage(text, outputType.value)
        val headers = createHeaders(outputType)

        return ResponseEntity.ok().headers(headers).body(file)
    }

    private fun createHeaders(outputType: UmlOutput.UmlOutputType): HttpHeaders {
        val headers = HttpHeaders()
        headers.add("Content-Type", outputType.getContentType())

        return headers
    }
}