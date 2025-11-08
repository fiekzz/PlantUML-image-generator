package com.fiekzz.com.puml.utils.debug

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

inline fun <reified T> logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

@Slf4j
@Component
class AppLogger {

//    fun info(message: String) {
//        val logger = getCallerLogger()
//        logger.info(message)
//    }

    /*
    * I have class A and class B. class A has @component and class B has injected class A. In class A there is a function to print the class that injected it. when class B call the function from class A, it will print the class B name. How can I do it in springboot kotlin just give me code
    * How can I make my logger like this which will be available to every files and do not need to do extra work just to log

        AppLogger.i(CurrentClass) { "This being printed" }
    *
    * */
//
//    fun debug(message: String) {
//        val logger = getCallerLogger()
//        logger.debug(message)
//    }
//
//    fun error(message: String) {
//        val logger = getCallerLogger()
//        logger.error(message)
//    }


//    val logger = getCallerLogger()

    private fun getCallerLogger(): org.slf4j.Logger {
        val callerClassName = Thread.currentThread().stackTrace
            .firstOrNull {
                it.className != this::class.java.name &&
                        it.className != Thread::class.java.name &&
                        !it.className.startsWith("java.lang.") &&
                        !it.className.startsWith("sun.") &&
                        !it.className.startsWith("jdk.internal.")
            }?.className ?: "Unknown"

        return LoggerFactory.getLogger(callerClassName)
    }

}