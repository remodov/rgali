package ru.insoft.rgali.model.opis

import ru.insoft.rgali.entity.FundFullEntity
import ru.insoft.rgali.entity.OpisEntity

data class OpisListModel (
    var opises: List<OpisEntity> = mutableListOf(),
    var fund : FundFullEntity? = null
)