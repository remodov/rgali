package ru.insoft.rgali.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
data class ApplicationProperties(
    var imagePath: String? = null,
    var host: String? = null,
    var emailFrom: String? = null,
    var emailTour: String? = null
)

