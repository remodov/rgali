package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import ru.insoft.rgali.dto.SelectDto
import java.sql.ResultSet

@Repository
class PurposeResearchDAO(
    private val jdbcTemplate: JdbcTemplate
) {
    fun findAll(name: String?): List<SelectDto> {
        return jdbcTemplate.query(
            if (name == null || name.isBlank()) SELECT_ALL else SELECT_ALL + WHERE_SECTION.format(name)
        ) { resultSet: ResultSet, _: Int ->
            SelectDto(
                id = resultSet.getString("id"),
                text = resultSet.getString("name")
            )
        }
    }

    fun find(id: Long): SelectDto? =
        jdbcTemplate.query(
            SELECT_ONE,
            ResultSetExtractor {
                if (it.next()) {
                    SelectDto(
                        id = it.getString("id"),
                        text = it.getString("name")
                    )
                }
                else null
            },
            id
        )

    companion object {
        private const val SELECT_ONE = """ 
            select id, name
            from public.purpose_research_type
            where id = ?
            """

        private const val SELECT_ALL = """ 
            select id, name
            from public.purpose_research_type
            """

        private const val WHERE_SECTION = """
               where name ~*'%s'
        """
    }
}