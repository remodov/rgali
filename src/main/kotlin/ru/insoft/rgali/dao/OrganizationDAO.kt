package ru.insoft.rgali.dao

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.insoft.rgali.entity.OrganizationEntity
import ru.insoft.rgali.entity.OrganizationShortEntity
import java.sql.ResultSet

@Repository
class OrganizationDAO(
    private val jdbcTemplate: JdbcTemplate
) {

    fun findAllByName(organizationName: String): List<OrganizationEntity> =
        jdbcTemplate.query(
            SELECT_ORGANIZATION.format("%$organizationName%")
        ) { resultSet: ResultSet, _: Int ->
            OrganizationEntity(
                orgId = resultSet.getLong("id"),
                orgName = resultSet.getString("org_name")
            )
        }

    fun getOrganizationById(id: Long): OrganizationEntity? =
        try {
            jdbcTemplate.queryForObject(
                SELECT,
                { resultSet: ResultSet, _: Int ->
                    OrganizationEntity(
                        orgName = resultSet.getString("org_name"),
                        years = resultSet.getString("years"),
                        annotation = resultSet.getString("annotation")
                    )
                },
                id
            )
        } catch (e: Exception) {
            logger.error("Error when try to find an organization : $id : ${e.message} ", e)
            OrganizationEntity()
        }

    fun getOrganizationsByFondId(fondId: Long): List<OrganizationEntity> {
        return jdbcTemplate.query(
            SELECT_ORGANIZATION_BY_FOND,
            { resultSet: ResultSet, _: Int ->
                OrganizationEntity(
                    orgId = resultSet.getLong("org_id"),
                    orgName = resultSet.getString("org_name")
                )
            },
            fondId
        )
    }

    fun getOrganizationsAuthorByDealId(dealID: Long): List<OrganizationEntity> {
        return jdbcTemplate.query(
            SELECT_ORGANIZATION_AUTHOR_BY_DEAL,
            { resultSet: ResultSet, _: Int ->
                OrganizationEntity(
                    orgId = resultSet.getLong("org_id"),
                    orgName = resultSet.getString("org_name")
                )
            },
            dealID
        )
    }

    fun getOrganizationsByDealId(dealID: Long): List<OrganizationEntity> {
        return jdbcTemplate.query(
            SELECT_ORGANIZATION_BY_DEAL,
            { resultSet: ResultSet, _: Int ->
                OrganizationEntity(
                    orgId = resultSet.getLong("org_id"),
                    orgName = resultSet.getString("org_name")
                )
            },
            dealID
        )
    }

    fun getOrganizationsByFondName(fondName: String): List<OrganizationShortEntity> {
        return jdbcTemplate.query(
            SELECT_SEARCH.format("%$fondName%")
        ) { resultSet: ResultSet, _: Int ->
            OrganizationShortEntity(
                id = resultSet.getLong("id"),
                orgName = resultSet.getString("org_name"),
                fondCount = resultSet.getString("fond_count")?.toInt(),
                dealAuthorCount = resultSet.getString("deal_org_author_count")?.toInt(),
                dealPersonCount = resultSet.getString("deal_count")?.toInt()
            )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(OrganizationDAO::class.java)

        private const val SELECT = """
              select o.id, 
                     o.org_name, 
                     o.years, 
                     o.annotation 
              from v_org o 
              where o.id = ?
              """

        private const val SELECT_SEARCH = """
            select vg.id, 
                   vg.org_name, 
                   (select count(1) from v_fond_org where org_id = vg.id) fond_count,
                   (select count(1) from v_deal_org_author where org_id = vg.id) deal_org_author_count,
                   (select count(1) from v_deal_org where org_id = vg.id) deal_count
            from v_org VG 
            where upper(VG.org_name) like upper('%s')
            """

        private const val SELECT_ORGANIZATION = """
             select id, 
                    org_name 
             from v_org
             where upper(org_name) like upper('%s')
              """

        private const val SELECT_ORGANIZATION_BY_FOND = """
             select org_id, 
                    org_name 
             from v_fond_org
             where fond_id = ?
              """

        private const val SELECT_ORGANIZATION_AUTHOR_BY_DEAL = """
            select org_id, case when org_role_type is null then org_name else org_role_type || ',' || org_name end as org_name
            from v_deal_org_author
            where deal_id = ?
              """

        private const val SELECT_ORGANIZATION_BY_DEAL = """
            select org_id, case when org_role_type is null then org_name else org_role_type || ',' || org_name end as org_name
            from v_deal_org
            where deal_id = ?
              """
    }
}