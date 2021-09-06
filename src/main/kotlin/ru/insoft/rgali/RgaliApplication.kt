package ru.insoft.rgali

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.insoft.rgali.config.ApplicationProperties


@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class RgaliApplication

fun main(args: Array<String>) {
    runApplication<RgaliApplication>(*args)
}