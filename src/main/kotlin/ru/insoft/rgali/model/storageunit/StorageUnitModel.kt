package ru.insoft.rgali.model.storageunit

import ru.insoft.rgali.entity.*

data class StorageUnitModel(
    var storageUnit: StorageUnitEntity? = null,
    var fund : FundFullEntity? = null
)