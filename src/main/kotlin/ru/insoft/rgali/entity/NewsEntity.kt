package ru.insoft.rgali.entity

import java.time.LocalDate

data class NewsEntity(
    val id: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val newsDate: LocalDate? = null,
    val mainImageId: Long? = null,
    val childContent: String? = null,
    val childId: Long? = null,
    val isNew: Boolean? = null
)