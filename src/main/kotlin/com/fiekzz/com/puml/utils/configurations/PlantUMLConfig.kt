package com.fiekzz.com.puml.utils.configurations

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class PlantUMLConfig {

    @PostConstruct
    fun init() {
        // Force headless mode for AWT
        System.setProperty("java.awt.headless", "true")
    }
}