package com.fiekzz.com.puml.model.plantuml

import javax.validation.constraints.*
import javax.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

data class PlantUMLRequest(
    val file: MultipartFile? = null,
    val text: String
)