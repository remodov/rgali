package ru.insoft.rgali.model.template

import ru.insoft.rgali.controller.Article
import ru.insoft.rgali.dao.PageSite
import ru.insoft.rgali.entity.Page

data class TemplateModel (
    var pages: List<Article?> = mutableListOf(),
    var page: Page = Page()
)