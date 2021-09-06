package ru.insoft.rgali.entity

data class SystematizationEntity (
    val id: Long,
    val parentId: Long?,
    val fullName: String,
    val level: Int,
    val dealAmountTotal: Int,
    var isLeaf: Boolean = false
)