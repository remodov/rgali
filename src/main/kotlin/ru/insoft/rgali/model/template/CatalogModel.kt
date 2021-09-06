package ru.insoft.rgali.model.template

import ru.insoft.rgali.entity.CatalogEntity
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort

data class CatalogModel (
    var pages: List<CatalogEntity?> = mutableListOf(),
    var page: Page = Page(),
    var sort: Sort = Sort()
)