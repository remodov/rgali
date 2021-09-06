package ru.insoft.rgali.entity

import java.time.LocalDate

data class NewsShortEntity(
    val id: Long? = null,
    val title: String? = null,
    val shortContent: String? = null,
    val newsDate: LocalDate? = null,
    val mainImageId: Long? = null
)