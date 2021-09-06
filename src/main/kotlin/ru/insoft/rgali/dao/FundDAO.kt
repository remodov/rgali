package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.entity.FundEntity
import ru.insoft.rgali.entity.FundFullEntity
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort
import ru.insoft.rgali.utils.SearchUtils
import java.sql.ResultSet
import java.util.*

@Repository
class FundDAO(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getFonds(fundType: String?, sort: Sort, page: Page): Optional<List<FundEntity>> {
        val selectWithSort = String.format(SELECT_LIST, if (sort.fieldForSort == null) "num_f" else sort.fieldForSort)

        val result = jdbcTemplate.query(
            selectWithSort,
            { resultSet: ResultSet?, i: Int ->
                FundEntityRowMapper().mapRow(resultSet!!, i)
            },
            fundType, fundType, page.rowsPerPage, page.offset
        )
        return if (result.isEmpty()) Optional.empty() else Optional.of(result)
    }

    fun getFonds(fundType: String?, sort: Sort, page: Page, searchForm: SearchForm): Optional<List<FundEntity>> {
        val selectWithSort =
            if (searchForm.fondType == "1") {
                String.format(
                    SELECT_LIST_SEARCH_FOND,
                    SearchUtils.createWhereSectionFonds(searchForm),
                    if (sort.fieldForSort == null) "num_f" else sort.fieldForSort
                )
            } else {
                String.format(
                    SELECT_LIST_SEARCH,
                    SearchUtils.createWhereSectionDeals(searchForm),
                    if (sort.fieldForSort == null) "num_f" else sort.fieldForSort
                )
            }

        val result = jdbcTemplate.query(
            selectWithSort,
            { resultSet: ResultSet?, i: Int ->
                FundEntityRowMapper().mapRow(resultSet!!, i)
            },
            page.rowsPerPage, page.offset
        )
        return if (result.isEmpty()) Optional.empty() else Optional.of(result)
    }

    fun getFond(id: Long): Optional<FundFullEntity> {
        val result = jdbcTemplate.query(
            SELECT_ONE,
            { resultSet: ResultSet?, i: Int ->
                FundFullEntityRowMapper().mapRow(resultSet!!, i)
            },
            id
        )
        return if (result.isEmpty()) Optional.empty() else Optional.of(result[0])
    }

    fun getWareHouseId(fondNumber: Int?): Long? =
        jdbcTemplate.queryForObject(SELECT_WAREHOUSE, Long::class.java, fondNumber)

    fun getWareHouseName(wareHouseId: Long?): String? =
        jdbcTemplate.queryForObject(SELECT_WAREHOUSE_NAME, String::class.java, wareHouseId)

    fun countFonds(fundType: String?): Long {
        return jdbcTemplate.queryForObject(SELECT_COUNT, Long::class.java, fundType, fundType)
    }

    fun countFonds(fundType: String?, searchForm: SearchForm): Long {
        return if (searchForm.fondType == "1") {
           jdbcTemplate.queryForObject(String.format(SELECT_COUNT_SEARCH_FUND, SearchUtils.createWhereSectionFonds(searchForm)), Long::class.java) ?: 0L
       } else {
           jdbcTemplate.queryForObject(String.format(SELECT_COUNT_SEARCH, SearchUtils.createWhereSectionDeals(searchForm)), Long::class.java) ?: 0L
       }
    }

    private inner class FundEntityRowMapper : RowMapper<FundEntity> {
        override fun mapRow(resultSet: ResultSet, rowNum: Int): FundEntity {
            return FundEntity(
                fondNumber = resultSet.getLong("num_f"),
                extremeDates = resultSet.getString("edgedates"),
                fullFondName = resultSet.getString("fond_name"),
                id = resultSet.getLong("id"),
                units = resultSet.getLong("units"),
                displayFondNumber = resultSet.getString("disp_num_f")
            )
        }
    }

    private inner class FundFullEntityRowMapper : RowMapper<FundFullEntity> {
        private val fundEntityRowMapper: FundEntityRowMapper = FundEntityRowMapper()

        override fun mapRow(resultSet: ResultSet, rowNum: Int): FundFullEntity {
            val fundEntity = fundEntityRowMapper.mapRow(resultSet, rowNum)

           return FundFullEntity(fundEntity).also {
                it.fondName = resultSet.getString("fond_name")
                it.fondType = resultSet.getString("type_name")
                it.createYear = resultSet.getString("create_year")
                it.opises = resultSet.getLong("opises")
                it.prints = resultSet.getLong("prints")

                val annotation = resultSet.getString("annotation")
                if (annotation != null) {
                    it.annotation = listOf(*annotation.split("\n".toRegex()).toTypedArray())
                }
            }
        }
    }

    companion object {
        private const val SELECT_ONE = """
            select  F.id, 
                    F.num_f,
                    F.disp_num_f,
                    F.full_fond_name,
                    F.edgedates,
                    F.units,
                    F.opises,
                    F.prints,
                    F.create_year,
                    F.fond_name, 
                    F.type_name,
                    F.annotation
            from v_fond_site F
            where F.id = ?
            """

        private const val SELECT_WAREHOUSE = """
            select case 
                    when name_eng = 'institutions funds' then 1
                    when name_eng = 'united funds' then 1
                    when name_eng = 'personal funds' then 2
                    when name_eng = 'collections' then 2
                    when name_eng = 'archive collections' then 2
                   end WAREHOUSE_ID
            from v_fond_type ft inner join v_fond vf 
               on ft.id = vf.type_id 
            where vf.num_f = ? 
            """

        private const val SELECT_WAREHOUSE_NAME = """
                select wt.name 
                from public.warehouse_type wt 
                where wt.id = ?
            """

        private const val SELECT_LIST = """
            select  F.id, 
                    F.num_f,
                    F.disp_num_f,
                    F.fond_name,
                    F.edgedates,
                    F.units
            from v_fond_site F inner join v_fond_type FT
               on F.type_id = FT.id 
            where ('all' = ? OR case when FT.name_eng in ('archive collections', 'collections') then 'collections'
                  else replace(FT.name_eng ,' ', '-' ) end = ?
            )
            order by F.%s  
            limit ?
            offset ?
        """

        private const val SELECT_LIST_SEARCH = """
            select  F.id, 
                    F.num_f,
                    F.disp_num_f,
                    F.fond_name,
                    F.edgedates,
                    F.units
            from v_fond_site F inner join v_fond_type FT
               on F.type_id = FT.id 
            where F.id in (
                   select fond_id
                   from site.v_deal_search
                   where 1 = 1
                %s
            )  
            order by F.%s  
            limit ?
            offset ?
        """

        private const val SELECT_LIST_SEARCH_FOND = """
            select  F.id, 
                    F.num_f,
                    F.disp_num_f,
                    F.fond_name,
                    F.edgedates,
                    F.units
            from v_fond_site F inner join v_fond_type FT
               on F.type_id = FT.id 
            where F.id in (
                   select id
                   from site.v_fond_search
                   where 1 = 1
                %s
            )  
            order by F.%s  
            limit ?
            offset ?
        """

        private const val SELECT_COUNT =
            """
                select  count(*) 
                from v_fond_site F inner join v_fond_type FT 
                        on F.type_id = FT.id 
                where ('all' = ? OR case when FT.name_eng in ('archive collections', 'collections') then 'collections'
                        else replace(FT.name_eng ,' ', '-' ) end = ?)
        """

        private const val SELECT_COUNT_SEARCH =
            """
                select count(*) 
                from v_fond_site F  
                where F.id in (
                       select fond_id
                       from site.v_deal_search
                       where 1 = 1
                        %s
                      )
        """

        private const val SELECT_COUNT_SEARCH_FUND =
            """
                select count(*) 
                from v_fond_site F  
                where F.id in (
                   select id
                   from site.v_fond_search
                   where 1 = 1
                        %s
               )
        """
    }
}