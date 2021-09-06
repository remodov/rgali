package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.controller.dateFormat
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.*
import ru.insoft.rgali.utils.SearchUtils
import java.sql.ResultSet
import java.time.LocalDate
import java.util.*

@Repository
class HolidayDAO(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun isHoliday(date: LocalDate): Boolean =
        jdbcTemplate.query(SELECT_ONE, ResultSetExtractor { it.next() }, date)!!

    companion object {
        private const val SELECT_ONE = """
                select hdate 
                from public.holidays  
                where hdate = ?
            """
    }
}