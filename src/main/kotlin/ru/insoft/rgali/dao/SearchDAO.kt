package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.entity.SearchEntity
import ru.insoft.rgali.utils.SearchUtils
import java.sql.ResultSet
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future


@Repository
class SearchDAO(private val jdbcTemplate: JdbcTemplate) {

    fun getSearchResults(searchForm: SearchForm, personId: Long? = null): SearchEntity? {
        if (personId == null) {
            return SearchEntity(
                totalUnits = calculateFonds(searchForm)?.toInt(),
                totalDeals = calculateDeals(searchForm)?.toInt(),
                totalOrganizations = calculateOrganizations(searchForm)?.toInt(),
                totalPersons = calculatePersons(searchForm)?.toInt()
            )
        } else {
            val calculatePersonDeals = calculatePersonStats(personId)
            return SearchEntity(
                totalUnits = calculatePersonDeals?.fonds?.toInt(),
                totalDeals = calculatePersonDeals?.deals?.toInt(),
                totalOrganizations = calculateOrganizations(searchForm)?.toInt(),
                totalPersons = calculatePersonDeals?.dealsAuth?.toInt()
            )
        }
    }

    fun calculatePersons(searchForm: SearchForm): String? {
        if (searchForm.personaName?.isBlank() == true) {
            return "0"
        }

        return jdbcTemplate.queryForObject(SELECT_PERSON_SEARCH + SearchUtils.createWhereSectionPersons(searchForm), String::class.java)
    }

    fun calculateOrganizations(searchForm: SearchForm): String? {
        if (searchForm.organizationName?.isBlank() == true) {
            return "0"
        }

        return jdbcTemplate.queryForObject(SELECT_ORGANIZATION_SEARCH + SearchUtils.createWhereSectionOrganizations(searchForm), String::class.java)
    }

    fun calculateDeals(searchForm: SearchForm): String? {
        return jdbcTemplate.queryForObject(SELECT_DEAL_SEARCH + SearchUtils.createWhereSectionDeals(searchForm), String::class.java)
    }

    fun calculatePersonStats(personId: Long): PersonStats? {
        return jdbcTemplate.query(SELECT_PERSON_STAT, ResultSetExtractor { rs: ResultSet ->
            if (rs.next()) {
                PersonStats(
                    fonds = rs.getString("fond_count"),
                    deals = rs.getString("deal_count"),
                    dealsAuth = rs.getString("deal_auth_count")
                )
            } else null
        }, personId)
    }

    fun calculateFonds(searchForm: SearchForm): String? {
        return jdbcTemplate.queryForObject(SELECT_FOND_SEARCH + SearchUtils.createWhereSectionDeals(searchForm), String::class.java)
    }

    companion object {
        private const val SELECT_FOND_SEARCH = """ 
            select count(distinct fond_id) total
            from site.v_deal_search
            where 1 = 1
            """

        private const val SELECT_DEAL_SEARCH = """
             select count(1) total  
             from v_deal_search
             where 1 = 1
            """

        private const val SELECT_PERSON_SEARCH = """
             select count(1) total
             from v_person
             where 1 = 1
            """

        private const val SELECT_ORGANIZATION_SEARCH = """
             select count(1) total
             from v_org
             where 1 = 1
            """

        private const val SELECT_PERSON_STAT = """
           select fond_count, deal_auth_count, deal_count
           from site.person_stat
           where person_id = ?
        """
    }
}

data class PersonStats(
    val fonds: String?,
    val dealsAuth: String?,
    val deals: String?
)