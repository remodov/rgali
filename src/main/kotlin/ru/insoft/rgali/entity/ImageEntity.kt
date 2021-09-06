package ru.insoft.rgali.entity

data class ImageEntity(
    val id: Long? = null,
    val title: String? = null,
    val path: String? = null,
    val img: String = "/image/$id/m"
)