package ru.insoft.rgali.model.fund

import ru.insoft.rgali.entity.FundEntity
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort

data class FundListModel (
    var funds: List<FundEntity> = mutableListOf(),
    var page: Page = Page(),
    var sort: Sort = Sort()
)