package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.entity.*
import ru.insoft.rgali.utils.SearchUtils
import java.sql.ResultSet
import java.time.LocalDate
import java.util.*

@Repository
class ImageDAO(private val jdbcTemplate: JdbcTemplate) {

    fun findOne(imageId: Long): ImageEntity? =
        jdbcTemplate.query(
            SELECT_ONE,
            ResultSetExtractor
            {
                it.next()
                ImageEntity(
                    id = it.getLong("id"),
                    title = it.getString("note"),
                    path = it.getString("full_file_name")
                )
            },
            imageId
        )

    fun findAll(newsId: Long): List<ImageSite> =
        jdbcTemplate.query(
            SELECT_LIST,
            { it: ResultSet, _: Int ->
                ImageSite(
                    imageId = it.getString("id"),
                    note = it.getString("note"),
                    subHtml  = it.getString("note"),
                    src = "/image/${it.getString("id")}/m",
                    thumb = "/image/${it.getString("id")}/m",

                    )
            },
            newsId
        )

    companion object {
        private const val SELECT_ONE = """
            select I.id, I.full_file_name, NI.note
            from public.images I left join v_news_images NI
                    on NI.id = I.id
            where I.id = ?
        """

        private const val SELECT_LIST = """
            select I.id, I.full_file_name, NI.note
            from public.images I left join v_news_images NI
                   on NI.id = I.id
            where NI.news_id = ?
            order by NI.serialnum
        """
    }
}