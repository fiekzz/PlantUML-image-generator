package com.fiekzz.com.puml

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableScheduling
class PumlApplication

fun main(args: Array<String>) {
	runApplication<PumlApplication>(*args)
}