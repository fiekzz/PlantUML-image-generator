package com.fiekzz.com.puml

import com.fiekzz.com.puml.utils.cache.UMLCache
import com.fiekzz.com.puml.utils.configurations.PlantUMLRuntimeHints
import com.fiekzz.com.puml.utils.runtimehints.AppRunTimeHints
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.scheduling.annotation.EnableScheduling

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

//@ComponentScan(basePackages = ["com.fiekzz.com.puml"]) // Adjust as needed
@SpringBootApplication
@EnableScheduling
@ImportRuntimeHints(AppRunTimeHints::class)
@RegisterReflectionForBinding(UMLCache::class)
class PumlApplication

fun main(args: Array<String>) {
	runApplication<PumlApplication>(*args)
}