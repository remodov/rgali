package ru.insoft.rgali.entity

data class AskEntity (
    val id: Long? = null,
    var askNum: Long? = null,
    val requestDate: String? = null,
    var askDate: String? = null,
    val askStateName: String? = null,
    val cypher: String? = null,
    val headerDoc: String? = null,

    val numLists: String? = null,
    val warehouseName: String? = null,
    val purposeResearchName: String? = null,
    var themename: String? = null,

    val num_f: String? = null,
    val num_o: String? = null,
    val num_d: String? = null,
    var dealId: Long? = null,
    val askStateTypeId: Long? = null,
    val warehouseTypeId: Long? = null,
    val purposeResearchTypeId: Long? = null,
    var researchThemeId: Long? = null,
    var researcherId: Long? = null,

    val literalOpis: String? = null,
    val literalDeal: String? = null,
)