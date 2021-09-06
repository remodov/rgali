package ru.insoft.rgali.model.opis

import ru.insoft.rgali.entity.FundFullEntity
import ru.insoft.rgali.entity.OpisEntity

data class OpisCardModel(
    var opis: OpisEntity? = null,
    var fund: FundFullEntity? = null
)