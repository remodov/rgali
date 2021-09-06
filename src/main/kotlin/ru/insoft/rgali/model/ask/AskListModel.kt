package ru.insoft.rgali.model.ask

import ru.insoft.rgali.entity.AskEntity
import ru.insoft.rgali.entity.FundEntity
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort

data class AskListModel (
    var asks: List<AskEntity> = mutableListOf(),
    var page: Page = Page(),
    var sort: Sort = Sort()
)