package com.fiekzz.com.puml.model.plantuml
import javax.validation.constraints.*
import javax.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class PlantUMLTextRequest(
    @field:NotBlank(message = "Plantuml text")
    val text: String,
    val outputType: String = "PNG",
)