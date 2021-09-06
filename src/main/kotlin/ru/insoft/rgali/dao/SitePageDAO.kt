package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SitePageDAO(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getMenuPages(menuId: Long): List<PageSite>? =
        jdbcTemplate.query(
            SELECT_PAGE,
            { it, _ ->
                PageSite(
                    textPage = it.getString("text_page"),
                    year = it.getString("year"),
                    id = it.getLong("id")
                )
            },
            menuId
        )

    fun getPageImages(pageId: Long): List<ImageSite>? =
        jdbcTemplate.query(
            SELECT_PAGE_IMAGES,
            { it, _ ->
                ImageSite(
                    imageId = it.getString("image_id"),
                    note = it.getString("note"),
                    subHtml  = it.getString("note"),
                    src = "/image/${it.getString("image_id")}/n",
                    thumb = "/image/${it.getString("image_id")}/n",
                )
            },
            pageId
        )

    fun getPageFiles(pageId: Long): List<PageFile>? =
        jdbcTemplate.query(
            SELECT_PAGE_FILES,
            { it, _ ->
                PageFile(
                    id = it.getString("id"),
                    fileName = it.getString("file_name"),
                )
            },
            pageId
        )

    companion object {
        const val SELECT_PAGE_IMAGES = """
          select image_id, note 
          from public.site_page_images
          where page_id = ?
          order by order_num 
        """

        const val SELECT_PAGE = """
            select id, text_page, year
            from public.site_page
            where menu_id = ?
              and site = true
             order by year desc, order_num
        """

        const val SELECT_PAGE_FILES = """
            select id, file_name
            from public.site_page_files
            where page_id = ?
            order by order_num
        """
    }
}

data class PageSite(
    var id: Long?,
    var textPage: String?,
    var year: String?,
)

data class ImageSite(
    var imageId: String?,
    var src: String?,
    var thumb: String?,
    var note: String?,
    var subHtml : String?
)

data class PageFile(
    var id: String?,
    var fileName: String?
)