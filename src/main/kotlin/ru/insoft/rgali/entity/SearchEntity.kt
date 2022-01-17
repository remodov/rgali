package ru.insoft.rgali.entity

data class SearchEntity(
    var totalUnits: Int? = null,
    var totalDeals: Int? = null,
    var totalOrganizations: Int? = null,
    var totalPersons: Int? = null,
    var totalPagesSrc: String? = null
)