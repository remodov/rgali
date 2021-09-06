package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.insoft.rgali.controller.dateFormat
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.*
import ru.insoft.rgali.service.DateService
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate
import java.util.*


@Repository
class AskDAO(
    private val jdbcTemplate: JdbcTemplate,
    private val fundDAO: FundDAO,
    private val researchThemeDAO: ResearchThemeDAO,
    private val storageUnitDAO: StorageUnitDAO,
    private val holidayDAO: HolidayDAO,
    private val dateService: DateService
) {

    fun countOpenedTasks(rgaliUserId: Long, askDate: LocalDate) =
        jdbcTemplate.queryForObject(SELECT_COUNT_OPEN_ASKS, Long::class.java, rgaliUserId, askDate)

    @Transactional
    fun closeAsk(askId: Long) {
        jdbcTemplate.update(
            """
              update public.ask
              set ask_state_type_id = 16
              where id = ?
            """, askId
        )

        jdbcTemplate.update(
            "insert into public.ask_state(ask_id, ask_state_type_id, ask_state_date, ask_state_time) " +
                " values (?, 16, current_date, current_time) ", askId
        )
    }


    fun findAll(researcherId: Long, sort: Sort, page: Page): List<AskEntity> {
        val selectWithSort = String.format(SELECT_ALL, if (sort.fieldForSort == null) "ASK.ask_num" else "ASK." + sort.fieldForSort)
        return jdbcTemplate.query(
            selectWithSort,
            { resultSet: ResultSet, _: Int ->
                AskEntity(
                    id = resultSet.getLong("id"),
                    askNum = resultSet.getLong("ask_num"),
                    requestDate = resultSet.getString("request_date"),
                    askDate = resultSet.getString("askdate"),
                    askStateName = resultSet.getString("ask_state_name"),
                    cypher = resultSet.getString("cypher"),
                    headerDoc = resultSet.getString("header_doc"),
                    numLists = resultSet.getString("num_sheet"),
                    warehouseName = resultSet.getString("warehouse_name")
                )
            },
            researcherId, page.rowsPerPage, page.offset
        )
    }

    fun findAskStates(askId: Long): List<AskStateEntity> {
        return jdbcTemplate.query(SELECT_ASK_STATE_ALL,
            { resultSet: ResultSet, _: Int ->
                AskStateEntity(
                    askStateName = resultSet.getString("ask_state_name"),
                    askStateDate = LocalDate.parse(resultSet.getString("ask_state_date")).format(dateFormat),
                    askStateTime = resultSet.getString("ask_state_time").let { it.split(":")[0] + ":" + it.split(":")[1] },
                    note = resultSet.getString("refusal_name"),
                )
            },
            askId
        )
    }

    fun findOne(askId: Long): AskEntity? {
        val askEntity = jdbcTemplate.query(
            SELECT_ONE,
            ResultSetExtractor {
                it.next()
                AskEntity(
                    id = it.getLong("id"),
                    askNum = it.getLong("ask_num"),
                    requestDate = LocalDate.parse(it.getString("request_date")).format(dateFormat),
                    askDate = LocalDate.parse(it.getString("askdate")).format(dateFormat),
                    askStateName = it.getString("ask_state_name"),
                    cypher = it.getString("cypher"),
                    headerDoc = it.getString("header_doc"),
                    numLists = it.getString("num_f"),
                    warehouseName = it.getString("warehouse_name"),
                    purposeResearchName = it.getString("purpose_research_name"),
                    themename = it.getString("user_ip"),
                )
            },
            askId
        )

        val researchTheme = if (askEntity?.themename?.matches(Regex("\\d+")) == true) {
                researchThemeDAO.find(askEntity.themename!!.toLong())?.text
        } else {
            askEntity?.themename
        }

        askEntity?.themename = researchTheme

        return askEntity
    }

    fun count(researcherId: Long): Long =
        jdbcTemplate.queryForObject(SELECT_COUNT, Long::class.java, researcherId)

    @Transactional
    fun save(askEntity: AskEntity) {
        val researchTheme = if (askEntity.themename?.matches(Regex("\\d+")) == true) {
            researchThemeDAO.find(askEntity.themename?.toLong()!!)
        } else {
            SelectDto(text = askEntity.themename, id = null)
        }

        askEntity.themename = researchTheme?.text
        askEntity.researchThemeId = researchTheme?.id?.toLong()

        val dealByKey = storageUnitDAO.findDealByKey(
            askEntity.num_f!!.toLong(),
            askEntity.num_o!!.toLong(),
            askEntity.num_d!!.toLong(),
            askEntity.literalOpis,
            askEntity.literalDeal
        )

        askEntity.dealId = dealByKey?.id
        val pkId = getPkId()

        val wareHouseId =
            if (dealByKey?.id != null && storageUnitDAO.isDealCopy(dealByKey.id!!) == true) 3 else fundDAO.getWareHouseId(askEntity.num_f.toInt())

        val askDate =
            if (askEntity.askDate?.contains(".") == true)
                LocalDate.parse(askEntity.askDate, dateFormat)
            else LocalDate.parse(askEntity.askDate)

        var returnDate = when {
                wareHouseId == 3L -> dateService.getAskDate(askDate,20)
                dealByKey?.importantCategoryId == null -> dateService.getAskDate(askDate,20)
                else -> dateService.getAskDate(askDate,10)
            }

        jdbcTemplate.update(
            INSERT,
            pkId,
            askEntity.askNum,
            askDate,
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
            returnDate,
            dealByKey?.barcode
        )

        jdbcTemplate.update(
            "insert into public.ask_state(ask_id, ask_state_type_id, ask_state_date, ask_state_time) " +
                    " values (?, 9, current_date, current_time) ", pkId
        )

        researchThemeDAO.save(
            ResearchThemeEntity(
                researcherId = askEntity.researcherId,
                researchThemeId = askEntity.researchThemeId,
                purposeResearchTypeId = askEntity.purposeResearchTypeId,
                theme = askEntity.themename
            ))
    }

    fun getId(): Long =
        jdbcTemplate.queryForObject(SELECT_NEW_ID, Long::class.java)!!

    fun getPkId(): Long =
        jdbcTemplate.queryForObject(SELECT_NEW_PK_ID, Long::class.java)!!

    fun isDealRequested(dealId: Long): Boolean =
        jdbcTemplate.queryForObject(SELECT_COUNT_DEALS, Long::class.java, dealId) > 0

    companion object {
        private const val SELECT_NEW_ID = """
                select nextval('public.ask_num_seq')
            """
        private const val SELECT_NEW_PK_ID = """
                select nextval('public.ask_id_seq')
            """
        private const val SELECT_ALL = """
                select ASK.id, 
                       ASK.ask_num, 
                       to_char(ASK.request_date, 'dd.mm.yyyy') request_date, 
                       to_char(ASK.askdate, 'dd.mm.yyyy') askdate,
                       ASK.ask_state_name, 
                       ASK.cypher, 
                       ASK.header_doc,
                       ASK.warehouse_name,
                       A.num_sheet
                from v_ask_site ASK inner join public.ask A
                      on ASK.id = A.id
                where ASK.researcher_id = ?
                order by %s desc 
                limit ?
                offset ?
            """

        private const val SELECT_ONE = """
                select s.id, 
                       s.ask_num, 
                       s.request_date, 
                       s.askdate, 
                       s.ask_state_name, 
                       s.cypher, 
                       s.header_doc,
                       s.num_f, 
                       s.warehouse_name,
                       s.purpose_research_name,
                       a.user_ip 
                from v_ask_site s inner join public.ask a
                    on a.id = s.id
                where s.id = ?
            """

        private const val SELECT_ASK_STATE_ALL = """
                select ask_state_name, 
                       ask_state_date, 
                       ask_state_time, 
                       refusal_name  
                from site.v_ask_state
                where ask_id = ?
                order by ask_state_date, ask_state_time
            """

        private const val SELECT_COUNT =
            """
                select count(*) 
                from v_ask_site 
                where researcher_id = ?
            """

        private const val SELECT_COUNT_OPEN_ASKS =
            """
                select count(*) from v_ask_site_activ where researcher_id = ? and askdate = ?         
            """

        private const val SELECT_COUNT_DEALS =
            """
                select count(*)
                from v_ask_site_activ
                where deal_id = ?         
            """

        private const val INSERT =
            """
                insert into public.ask(
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
                    ?,?,current_date,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?
                )
            """
    }
}