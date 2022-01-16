package ru.insoft.rgali.dao

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.insoft.rgali.entity.PersonEntity
import ru.insoft.rgali.entity.PersonShortEntity
import java.sql.ResultSet
import java.util.*

@Repository
class PersonDAO(
    private val jdbcTemplate: JdbcTemplate
) {
    fun findAll(personName: String): List<PersonShortEntity> {
        return jdbcTemplate.query(
            SELECT_ALL.format("%$personName%")
        ) { resultSet: ResultSet, _: Int ->
            PersonShortEntity(
                id = resultSet.getLong("id"),
                fio = resultSet.getString("fio")
            )
        }
    }

    fun getPersonsByFio(personFio: String?): List<PersonShortEntity> {
        return jdbcTemplate.query(
            SELECT_PERSON_SEARCH.format("%$personFio%")
        ) { resultSet: ResultSet, _: Int ->
            PersonShortEntity(
                id = resultSet.getLong("id"),
                fio = resultSet.getString("fio"),
                fondCount = resultSet.getInt("fond_count"),
                dealAuthorCount = resultSet.getInt("deal_author_count"),
                dealPersonCount = resultSet.getInt("deal_person_count")
            )
        }
    }

    fun getPersonsByFundId(fundId: Long?): Optional<List<PersonEntity>> {
        val result = jdbcTemplate.query(
            SELECT_ONE,
            { resultSet: ResultSet, _: Int ->
                PersonEntity(
                    id = resultSet.getLong("person_id"),
                    fio = resultSet.getString("fio")
                )
            },
            fundId
        )
        return Optional.ofNullable(result)
    }

    fun getPersonById(id: Long): PersonEntity? =
        try {
            jdbcTemplate.queryForObject(
                SELECT_PERSON,
                { resultSet: ResultSet, _: Int ->
                    PersonEntity(
                        fio = resultSet.getString("fio"),
                        personSynonym = resultSet.getString("person_synonym"),
                        years = resultSet.getString("years"),
                        personActivity = resultSet.getString("person_activity"),
                        annotation = resultSet.getString("annotation"),
                        id = id
                    )
                },
                id
            )
        } catch (e: Exception) {
            logger.error("Error when try to find an organization : $id : ${e.message} ", e)
            PersonEntity()
        }

    fun getDealAuthorPerson(dealId: Long) : List<PersonEntity> {
        return jdbcTemplate.query(
            """
                select case when  person_role is null then fio else fio ||','|| person_role end as full_fio, person_id
                from v_deal_person_author
                where deal_id = ?
            """.trimIndent(),
            { resultSet: ResultSet, _: Int ->
                PersonEntity(
                    id = resultSet.getLong("person_id"),
                    fio = resultSet.getString("full_fio")
                )
            },
            dealId
        )
    }

    fun getDealPerson(dealId: Long) : List<PersonEntity> {
        return jdbcTemplate.query(
            """
                select case when  person_role is null then fio else fio ||','|| person_role end as full_fio, person_id
                from v_deal_person
                where deal_id = ?
            """.trimIndent(),
            { resultSet: ResultSet, _: Int ->
                PersonEntity(
                    id = resultSet.getLong("person_id"),
                    fio = resultSet.getString("full_fio")
                )
            },
            dealId
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PersonDAO::class.java)

        private const val SELECT_ONE = """ 
            select FP.person_id, 
                   FP.fio
            from v_fond_person FP
            where FP.fond_id = ?
            """

        private const val SELECT_PERSON = """ 
            select o.fio, 
                   o.person_synonym,
                   o.years,
                   o.personactivity person_activity,  
                   o.annotation
            from v_person o 
            where o.id = ?
            """

        private const val SELECT_PERSON_SEARCH = """ 
            select vp.id, 
                   vp.fio, 
                   (select count(1) from v_fond_person where person_id = vp.id) fond_count,
                   (select count(1) from v_deal_person_author where person_id = vp.id) deal_author_count,
                   (select count(1) from v_deal_person where person_id = vp.id) deal_person_count
            from v_person VP 
            where upper(VP.fio) like upper('%s')
            """

        private const val SELECT_ALL = """ 
            select vp.id, 
                   vp.fio
            from v_person VP 
            where upper(VP.fio) like upper('%s')
            """
    }
}