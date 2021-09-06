package ru.insoft.rgali.entity

data class OrganizationEntity (
    var orgId: Long? = null,
    var orgName: String? = null,
    var years: String? = null,
    var annotation: String? = null
)

data class OrganizationShortEntity (
    var id: Long? = null,
    var orgName: String? = null,
    var fondCount: Int? = null,
    var dealAuthorCount: Int? = null,
    var dealPersonCount: Int? = null
)