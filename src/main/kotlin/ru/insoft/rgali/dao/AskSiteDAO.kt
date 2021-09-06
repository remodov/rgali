package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.controller.dateFormat
import ru.insoft.rgali.entity.*
import ru.insoft.rgali.utils.SearchUtils
import java.sql.ResultSet
import java.time.LocalDate
import java.util.*

@Repository
class AskSiteDAO(
    private val jdbcTemplate: JdbcTemplate,
    private val fundDAO: FundDAO,
    private val storageUnitDAO: StorageUnitDAO
) {
    fun findAll(researcherId: Long): List<AskEntity> {
        return jdbcTemplate.query(
            SELECT_ALL,
            { resultSet: ResultSet, _: Int ->
                AskEntity(
                    id = resultSet.getLong("id"),
                    cypher = resultSet.getString("cypher"),
                    headerDoc = resultSet.getString("header_doc"),
                    numLists = resultSet.getString("num_sheet"),
                    warehouseName = resultSet.getString("warehouse_name"),
                )
            },
            researcherId
        )
    }

    fun findOne(askId: Long): AskEntity? =
        jdbcTemplate.query(
            SELECT_ONE,
            ResultSetExtractor {
                it.next()
                AskEntity(
                    id = it.getLong("id"),
                    askNum = it.getLong("ask_num"),
                    cypher = it.getString("cypher"),
                    headerDoc = it.getString("header_doc"),
                    numLists = it.getString("num_sheet"),
                    num_o = it.getString("num_o"),
                    num_f = it.getString("num_f"),
                    num_d = it.getString("num_d"),
                    dealId = it.getLong("deal_id")
                )
            },
            askId
        )

    fun isDealExists(researcherId: Long, dealId: Long): Boolean? =
        jdbcTemplate.query(
            "select 1 from ask_site where deal_id = ? and researcher_id = ?",
            ResultSetExtractor { it.next() },
            dealId,
            researcherId
        )

    @Transactional
    fun save(askEntity: AskEntity) {
        val dealByKey = storageUnitDAO.findDealByKey(
            askEntity.num_f!!.toLong(),
            askEntity.num_o!!.toLong(),
            askEntity.num_d!!.toLong(),
            askEntity.literalOpis,
            askEntity.literalDeal
        )

        askEntity.dealId = dealByKey?.id

        askEntity.askDate = if (askEntity.askDate == null) LocalDate.now().toString() else askEntity.askDate

        val wareHouseId =
            if (storageUnitDAO.isDealCopy(dealByKey?.id!!) == true) 3 else fundDAO.getWareHouseId(askEntity.num_f.toInt())

        jdbcTemplate.update(
            INSERT,
            askEntity.askNum,
            if (askEntity.askDate?.contains(".") == true) LocalDate.parse(
                askEntity.askDate,
                dateFormat
            ) else LocalDate.parse(askEntity.askDate),
            askEntity.num_f,
            askEntity.num_o,
            askEntity.num_d,
            askEntity.cypher,
            askEntity.headerDoc,
            askEntity.dealId,
            9,
            wareHouseId,
            askEntity.purposeResearchTypeId,
            askEntity.themename,
            askEntity.researcherId,
            askEntity.numLists,
            askEntity.researchThemeId,
            8,
            when {
                wareHouseId == 3L -> LocalDate.now().plusDays(20)
                dealByKey.importantCategoryId == null -> LocalDate.now().plusDays(20)
                else -> LocalDate.now().plusDays(10)
            },
            dealByKey.barcode
        )
    }

    fun delete(askSiteId: Long) {
        jdbcTemplate.update(
            "delete from ask_site where id = ?", askSiteId
        )
    }

    fun getId(): Long =
        jdbcTemplate.queryForObject(SELECT_NEW_ID, Long::class.java)!!

    fun countInRecycle(userId: Long): Long =
        jdbcTemplate.queryForObject(SELECT_COUNT_SITE, Long::class.java, userId)

    companion object {
        private const val SELECT_NEW_ID = """
                select nextval('public.ask_num_seq')
            """

        private const val SELECT_COUNT_SITE = """
                select count(1) 
                from ask_site
                where researcher_id = ?
            """

        private const val SELECT_ALL = """
                select s.id,
                       s.num_sheet,
                       s.cypher, 
                       s.header_doc,
                       wt.name warehouse_name
                from ask_site s left join public.warehouse_type wt
                        on s.warehouse_type_id = wt.id
                where s.researcher_id = ?
                order by s.id desc
            """

        private const val SELECT_ONE = """
                select id, 
                       ask_num, 
                       cypher, 
                       header_doc,
                       num_f, 
                       num_o, 
                       num_d,
                       num_sheet,
                       deal_id
                from ask_site 
                where id = ?
            """

        private const val INSERT =
            """
                insert into ask_site(
                                    id,
                                    ask_num, 
                                    request_date, 
                                    askdate, 
                                    num_f, 
                                    num_o, 
                                    num_d, 
                                    cypher, 
                                    header_doc, 
                                    deal_id, 
                                    ask_state_type_id, 
                                    warehouse_type_id, 
                                    purpose_research_type_id, 
                                    user_ip, 
                                    researcher_id,
                                    num_sheet,
                                    research_theme_id,
                                    receipt_id,
                                    dreturn,
                                    bar_code)
                values (
                    nextval('public.ask_id_seq'),?,current_date,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?
                )
            """
    }
}