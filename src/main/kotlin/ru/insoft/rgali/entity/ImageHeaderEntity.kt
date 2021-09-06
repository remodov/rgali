package ru.insoft.rgali.entity

data class ImageHeaderEntity(
    val imageId: Long,
    val personId: Long,
    val fio: String,
    val fondId: Long,
    val fnumFull: String,
    val fullFileName: String
)