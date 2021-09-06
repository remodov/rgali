package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.insoft.rgali.dto.SelectDto
import java.sql.ResultSet

@Repository
class LiterDAO(
    private val jdbcTemplate: JdbcTemplate
) {
    fun findAll(): List<SelectDto> {
        return jdbcTemplate.query(
            SELECT_ALL
        ) { resultSet: ResultSet, _: Int ->
            SelectDto(
                id = resultSet.getString("name"),
                text = resultSet.getString("name")
            )
        }
    }

    companion object {
        private const val SELECT_ALL = """ 
                select id, name 
                from public.liter_cls
                order by name
            """
    }
}