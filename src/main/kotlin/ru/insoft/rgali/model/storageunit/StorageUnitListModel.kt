package ru.insoft.rgali.model.storageunit

import ru.insoft.rgali.entity.*

data class StorageUnitListModel(
    var storageUnits: MutableList<StorageUnitEntity> = mutableListOf(),
    var fund : FundFullEntity? = null,
    var opis : OpisEntity? = null,
    var systemPartition: SystemPartitionEntity? = null,
    var opisCount: Int? = null,
    var page: Page = Page(),
    var sort: Sort = Sort()
)