package ru.insoft.rgali.model.news

import ru.insoft.rgali.dao.ImageSite
import ru.insoft.rgali.entity.ImageEntity

data class NewsModel (
    val id: Long?,
    val day: String?,
    val month: String?,
    val year: String?,
    val title: String?,
    val shortInfo: String?,
    val imageId: Long?,
)

data class NewsExtendedModel (
    val day: String?,
    val month: String?,
    val year: String?,
    val title: String?,
    val fullInfo: String?,
    val imageId: Long?,
    val images: List<ImageSite>,
    val childId: Long?,
    val childTitle: String?,
    val isNew: Boolean?
)