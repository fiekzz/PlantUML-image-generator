package com.fiekzz.com.puml.utils.debug

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

inline fun <reified T> logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}