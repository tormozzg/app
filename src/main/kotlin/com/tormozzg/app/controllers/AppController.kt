package com.tormozzg.app.controllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/"])
class AppController {

    @Value("\${info.app.version:no_data}")
    lateinit var version: String

    @GetMapping
    fun appInfo(): ApplicationInfo {
        return ApplicationInfo("Welcome to App", version)
    }
}

class ApplicationInfo(
    val message: String,
    val version: String
)