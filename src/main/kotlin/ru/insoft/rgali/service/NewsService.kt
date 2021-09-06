package ru.insoft.rgali.service

import org.springframework.stereotype.Service
import ru.insoft.rgali.dao.ImageDAO
import ru.insoft.rgali.dao.NewsDAO
import ru.insoft.rgali.model.news.NewsExtendedModel
import ru.insoft.rgali.model.news.NewsModel
import java.time.format.TextStyle
import java.util.*

@Service
class NewsService(val newsDAO: NewsDAO, val imageDAO: ImageDAO) {

    fun allNews(): List<NewsModel> =
        newsDAO.findAll().map {
            NewsModel(
                id = it.id,
                day = it.newsDate?.dayOfMonth.toString().toUpperCase(),
                month = it.newsDate?.month?.getDisplayName(TextStyle.FULL, Locale("ru"))?.toUpperCase(),
                year = it.newsDate?.year.toString().toUpperCase(),
                title = it.title,
                shortInfo = it.shortContent,
                imageId = it.mainImageId,
            )
        }

    fun allArchNews(year: Int, month: Int, type: Int?): List<NewsModel> =
        newsDAO.findAllArch(year, month, type).map {
            NewsModel(
                id = it.id,
                day = it.newsDate?.dayOfMonth.toString().toUpperCase(),
                month = it.newsDate?.month?.getDisplayName(TextStyle.FULL, Locale("ru"))?.toUpperCase(),
                year = it.newsDate?.year.toString().toUpperCase(),
                title = it.title,
                shortInfo = it.shortContent,
                imageId = it.mainImageId,
            )
        }

    fun news(id: Long): NewsExtendedModel {
        val newsEntity = newsDAO.get(id)
        return NewsExtendedModel(
            day = newsEntity?.newsDate?.dayOfMonth.toString().toUpperCase(),
            month = newsEntity?.newsDate?.month?.getDisplayName(TextStyle.FULL, Locale("ru"))?.toUpperCase(),
            year = newsEntity?.newsDate?.year.toString().toUpperCase(),
            title = newsEntity?.title,
            fullInfo = newsEntity?.content,
            imageId = newsEntity?.mainImageId,
            images = imageDAO.findAll(id),
            childId = newsEntity?.childId,
            childTitle = newsEntity?.childContent,
            isNew = newsEntity?.isNew
        )
    }
}