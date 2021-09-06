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
class FilesDAO(private val jdbcTemplate: JdbcTemplate) {

    fun findOne(imageId: Long): PageFile? =
        jdbcTemplate.query(
            SELECT_ONE,
            ResultSetExtractor
            {
                if (it.next())
                    PageFile(
                        id = it.getString("id"),
                        fileName = it.getString("file_name")
                    ) else null
            },
            imageId
        )

    companion object {
        private const val SELECT_ONE = """
            select id, file_name
            from public.site_page_files
            where id = ?
        """
    }
}