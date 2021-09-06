package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.*
import ru.insoft.rgali.utils.SearchUtils
import java.sql.ResultSet
import java.time.LocalDate
import java.util.*

@Repository
class NewsDAO(private val jdbcTemplate: JdbcTemplate) {

    fun findAll(): List<NewsShortEntity> =
        jdbcTemplate.query(
            SELECT_LIST,
        ) { resultSet: ResultSet, _: Int ->
            NewsShortEntity(
                id = resultSet.getLong("id"),
                title = resultSet.getString("title"),
                shortContent = resultSet.getString("short_content"),
                newsDate = resultSet.getDate("news_date")?.toLocalDate(),
                mainImageId = resultSet.getLong("image_id")
            )
        }

    fun findAllArch(year: Int, month: Int, type: Int?): List<NewsShortEntity> =
        jdbcTemplate.query(
            if (type != null)
                SELECT_LIST_ARCH.format(WHERE_NEWS_TYPE.format(type))
            else
                SELECT_LIST_ARCH.format(""),
            { resultSet: ResultSet, _: Int ->
                NewsShortEntity(
                    id = resultSet.getLong("id"),
                    title = resultSet.getString("title"),
                    shortContent = resultSet.getString("short_content"),
                    newsDate = resultSet.getDate("news_date")?.toLocalDate(),
                    mainImageId = resultSet.getLong("image_id")
                )
            },
            year, month
        )



    fun getAttention(): NewsEntity? =
        jdbcTemplate.query(
            SELECT_ATTENTION,
            ResultSetExtractor {
                if (it.next()) {
                    NewsEntity(
                        title = it.getString("title"),
                        content = it.getString("content")
                    )
                }
                else
                    NewsEntity()
            }
        )

    fun findAllNewsType(): List<SelectDto> {
        return jdbcTemplate.query(
            NEWS_TYPE
        ) { resultSet: ResultSet, _: Int ->
            SelectDto(
                id = resultSet.getString("id"),
                text = resultSet.getString("name")
            )
        }
    }

    fun get(id: Long): NewsEntity? =
        jdbcTemplate.query(
            SELECT_ONE,
            ResultSetExtractor {
                it.next()
                NewsEntity(
                    id = it.getLong("id"),
                    title = it.getString("title"),
                    content = it.getString("content"),
                    newsDate = it.getDate("news_date")?.toLocalDate(),
                    mainImageId = it.getLong("image_id"),
                    childContent = it.getString("child_title"),
                    childId = it.getLong("child_id"),
                    isNew = it.getBoolean("is_new")
                )
            },
            id
        )

    companion object {
        private const val SELECT_ONE = """
            select N.id, 
                   N.title, 
                   N."content", 
                   N.news_date, 
                   NI.id image_id,
                   NP.id child_id,
                   NP.title child_title,
                   N.is_new
            from v_news_site N left join v_news_site NP
                   on NP.parent_id = N.id 
                 left join v_news_images NI 
                   on N.id = NI.news_id 
                  and NI.is_main = true 
            where N.id = ?
              and (N.is_fixed is null OR N.is_fixed = false) 
            order by NI.serialnum
            """

        private const val SELECT_ATTENTION = """
            select N.title, 
                   N."content" 
            from v_news_site N 
            where N.is_fixed = true
            """

        private const val SELECT_LIST = """
            select N.id, 
                   N.title, 
                   N.short_content, 
                   N.news_date, 
                   NI.id image_id
            from v_news_site N left join v_news_images NI 
                  on N.id  = NI.news_id 
                 and NI.is_main = true 
            where N.is_new = true
              and N.parent_id is null
              and (N.issue_date is null or current_date >= N.issue_date)
            order by N.news_date desc
        """

        private const val SELECT_LIST_ARCH = """
            select N.id,
                   N.title,
                   N.short_content,
                   N.news_date,
                   NI.id image_id
            from site.v_news_site N left join site.v_news_images NI
                   on N.id = NI.news_id
                  and NI.is_main = true
            where N.is_new = false
              and N.parent_id is null
              and EXTRACT(YEAR FROM N.news_date) = ?
              and EXTRACT(MONTH FROM N.news_date) = ?
              and (N.is_fixed is null OR N.is_fixed = false) 
              %s
            order by N.news_date desc
        """
        private const val WHERE_NEWS_TYPE = """
              and N.id in (
                select news_id
                from public.news_type_news
                where 1 = 1
                  and news_type_id = %s
              )
        """

        private const val NEWS_TYPE = """
             select id, name
             from public.news_type
        """

    }
}