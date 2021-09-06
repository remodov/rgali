package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import ru.insoft.rgali.controller.RegistrationForm
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.ResearchThemeEntity
import java.sql.ResultSet

@Repository
class ResearchThemeDAO(
    private val jdbcTemplate: JdbcTemplate
) {
    fun findAll(name: String?): List<SelectDto> {
        return jdbcTemplate.query(
            if (name == null || name.isBlank()) SELECT_ALL.format("") else SELECT_ALL.format(WHERE_SECTION.format(name))
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
                } else null
            },
            id
        )

    fun save(researchThemeEntity: ResearchThemeEntity) {
        jdbcTemplate.update(
            INSERT,
            researchThemeEntity.researcherId,
            researchThemeEntity.researchThemeId,
            researchThemeEntity.purposeResearchTypeId,
            researchThemeEntity.theme
        )
    }

    companion object {
        private const val SELECT_ALL = """ 
           select distinct id, name 
           from public.research_theme
           where name is not null
              %s
           order by name 

        """

        private const val WHERE_SECTION = """
               and name ~*'%s'
        """

        private const val SELECT_ONE = """ 
           select id, name 
           from public.research_theme
           where id = ?     
        """

        private const val INSERT =
            """
        insert into public.researcher_theme(researcher_id , research_theme_id, research_date, purpose_research_type_id, theme)
        values(?, ?, current_date, ?, ?)
            """
    }
}