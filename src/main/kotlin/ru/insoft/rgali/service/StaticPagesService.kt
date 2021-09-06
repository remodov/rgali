package ru.insoft.rgali.service

import org.springframework.stereotype.Service
import ru.insoft.rgali.controller.Article
import ru.insoft.rgali.controller.ArticleByYears
import ru.insoft.rgali.dao.MenuInfo
import ru.insoft.rgali.dao.SiteMenuDAO
import ru.insoft.rgali.dao.SitePageDAO

@Service
class StaticPagesService(
    private val menuDAO: SiteMenuDAO,
    private val pageSiteDAO: SitePageDAO
) {
    fun getYears(menuId: Long): List<String?>? = pageSiteDAO.getMenuPages(menuId)?.map { it.year }?.distinct()

    fun getPages(menuId: Long): List<ArticleByYears?>? =
        pageSiteDAO.getMenuPages(menuId)?.groupBy { it.year }?.map { k ->
            ArticleByYears(
                k.key, k.value.map {
                    Article(
                        text = it.textPage,
                        images = pageSiteDAO.getPageImages(it.id!!),
                        id = it.id!!,
                        files = pageSiteDAO.getPageFiles(it.id!!)
                    )
                }.toList(),
            )
        }?.toList()

    fun getArticles(menuId: Long): List<Article?>? =
        pageSiteDAO.getMenuPages(menuId)?.map {
                    Article(
                        text = it.textPage,
                        images = pageSiteDAO.getPageImages(it.id!!),
                        id = it.id!!,
                        files = pageSiteDAO.getPageFiles(it.id!!)
                    ) }?.toList()

    fun getMenuItemInfo(menuId: Long): MenuInfo? = menuDAO.getMenuInfo(menuId)
}