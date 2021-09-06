package ru.insoft.rgali.model.systematization

import ru.insoft.rgali.entity.FundFullEntity
import ru.insoft.rgali.entity.OpisEntity
import ru.insoft.rgali.entity.SystematizationEntity

data class SystematizationModel (
    val systematizationHierarchy: MutableList<SystematizationEntity> = mutableListOf(),
    var opis: OpisEntity? = null,
    var fund: FundFullEntity? = null
)