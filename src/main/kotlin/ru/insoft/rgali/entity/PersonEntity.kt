package ru.insoft.rgali.entity

data class PersonEntity (
    var id: Long? = null,
    var fio: String? = null,
    var personSynonym: String? = null,
    var years: String? = null,
    var personActivity: String? = null,
    var annotation: String? = null
)

data class PersonShortEntity (
    var id: Long? = null,
    var fio: String? = null,
    var fondCount: Int? = null,
    var dealAuthorCount: Int? = null,
    var dealPersonCount: Int? = null
)