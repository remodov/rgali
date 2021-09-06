package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import ru.insoft.rgali.controller.RegistrationForm
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.controller.StorageUnitController.Companion.SHIFR_VALUE
import ru.insoft.rgali.entity.*
import ru.insoft.rgali.utils.SearchUtils
import java.sql.ResultSet

@Repository
class StorageUnitDAO(
    private val jdbcTemplate: JdbcTemplate
) {

    fun findDealByKey(
        fondNumber: Long,
        opisNumber: Long,
        dealNumber: Long,
        suffixOpis: String?,
        suffixDeal: String?
    ): StorageUnitEntity? =
        jdbcTemplate.query(
            DEAL_SEARCH.plus(
                if (suffixDeal?.isNotBlank() == true && suffixDeal != "null") {
                    " and DS.suffix = '%s' ".format(suffixDeal)
                } else ""
            ).plus(
                if (suffixOpis?.isNotBlank() == true && suffixOpis != "null") {
                    " and DS.suffix_o = '%s' ".format(suffixOpis)
                } else ""
            ),
            ResultSetExtractor {
                if (it.next()) {
                    StorageUnitEntity(
                        id = it.getLong("id"),
                        title = it.getString("title"),
                        pages = it.getString("pages"),
                        barcode = it.getString("barcode"),
                        importantCategoryId = it.getString("important_category_id")
                    )
                } else null
            },
            fondNumber, opisNumber, dealNumber
        )

    fun isDealCopy(dealId: Long): Boolean? =
        jdbcTemplate.query(
            DEAL_COPY,
            ResultSetExtractor {
                it.next()
            },
            dealId
        )

    fun getStorageUnitsBySearch(searchForm: SearchForm, sort: Sort, page: Page): MutableList<StorageUnitEntity> {
        val selectWithSort =
            String.format(
                SELECT_ALL_SEARCH,
                SearchUtils.createWhereSectionDeals(searchForm),
                when (sort.fieldForSort) {
                    null -> SHIFR_SEARCH
                    SHIFR_VALUE -> SHIFR_SEARCH
                    else -> sort.fieldForSort
                }
            )

        return jdbcTemplate.query(
            selectWithSort,
            { resultSet: ResultSet, _: Int -> mapStorageUnitEntity(resultSet) },
            page.rowsPerPage, page.offset
        )
    }

    fun getStorageUnitsByOpisId(opisId: Long, sort: Sort, page: Page): MutableList<StorageUnitEntity> {
        return searchById(id = opisId, sort = sort, page = page, searchType = SearchType.OPIS_ID)
    }

    fun getStorageUnitsByFondId(fondId: Long, sort: Sort, page: Page): MutableList<StorageUnitEntity> {
        return searchById(id = fondId, sort = sort, page = page, searchType = SearchType.FOND_ID)
    }

    fun getStorageUnitsBySystemId(systemId: Long, sort: Sort, page: Page): MutableList<StorageUnitEntity> {
        return searchById(id = systemId, sort = sort, page = page, searchType = SearchType.SYSTEM_ID)
    }

    fun getStorageUnitById(storageUnitId: Long): StorageUnitEntity? {
        return jdbcTemplate.queryForObject(
            SELECT_ONE,
            { rs, _ ->
                mapStorageUnitEntity(resultSet = rs).also {
                    it.systemPartitionName = rs.getString(DEAL_SYSTEM_PARTITION_NAME_COLUMN)
                    it.fondId = rs.getLong(DEAL_FOND_ID_COLUMN)
                    it.person = rs.getString("deal_person")
                    it.personAuthor = rs.getString("deal_person_author")
                    it.systemPartition = rs.getString("deal_system_partition")
                    it.materialType = rs.getString("deal_material_type")
                    it.systemPartitionId = rs.getLong("system_partition_id")
                    it.opisId = rs.getLong("opis_id")
                    it.annotation = rs.getString("annotation")
                }
            },
            storageUnitId
        )
    }

    fun loadPlayMethod(id: Long): String {
        return jdbcTemplate.query(
            """
                    select play_method_id, name
                    from v_deal_play_method
                    where deal_id = ?
                """.trimIndent(),
            { resultSet: ResultSet, _: Int ->
                IdValueEntity(
                    id = resultSet.getLong("play_method_id"),
                    name = resultSet.getString("name")
                )
            },
            id
        ).joinToString(",") { it.name }
    }

    fun loadMaterialType(id: Long): String {
        return jdbcTemplate.query(
            """
                select material_type_id, name
                from v_deal_material_type
                where deal_id = ?
                """.trimIndent(),
            { resultSet: ResultSet, _: Int ->
                IdValueEntity(
                    id = resultSet.getLong("material_type_id"),
                    name = resultSet.getString("name")
                )
            },
            id
        ).joinToString(",") { it.name }
    }

    fun loadTheme(id: Long): List<IdValueEntity> {
        return jdbcTemplate.query(
            """
                select thema_id, full_name
                from v_deal_theme
                where deal_id = ?
                """.trimIndent(),
            { resultSet: ResultSet, _: Int ->
                IdValueEntity(
                    id = resultSet.getLong("thema_id"),
                    name = resultSet.getString("full_name")
                )
            },
            id
        )
    }

    fun loadDocumentGroup(id: Long): List<IdValueEntity> {
        return jdbcTemplate.query(
            """
              select document_group_id, full_document_group_name
              from v_deal_document_group
              where deal_id = ?
                """.trimIndent(),
            { resultSet: ResultSet, _: Int ->
                IdValueEntity(
                    id = resultSet.getLong("document_group_id"),
                    name = resultSet.getString("full_document_group_name")
                )
            },
            id
        )
    }

    fun loadRubrika(id: Long): List<IdValueEntity> {
        return jdbcTemplate.query(
            """
             select rubrika_id, rubrika_full_name
             from v_deal_rubrika
             where deal_id = ?
                """.trimIndent(),
            { resultSet: ResultSet, _: Int ->
                IdValueEntity(
                    id = resultSet.getLong("rubrika_id"),
                    name = resultSet.getString("rubrika_full_name")
                )
            },
            id
        )
    }

    fun loadPlace(id: Long): List<IdValueEntity> {
        return jdbcTemplate.query(
            """
            select case when place_type is null then full_address 
                        else full_address ||', '|| place_type 
                   end as place_type, 
                   place_type_id
            from v_deal_place
            where deal_id = ?
                """.trimIndent(),
            { resultSet: ResultSet, _: Int ->
                IdValueEntity(
                    id = resultSet.getLong("place_type_id"),
                    name = resultSet.getString("place_type")
                )
            },
            id
        )
    }

    private fun searchById(id: Long, sort: Sort, page: Page, searchType: SearchType): MutableList<StorageUnitEntity> {
        val selectWithSort =
            String.format(
                SELECT_ALL,
                when (searchType) {
                    SearchType.FOND_ID -> WHERE_FOND_ID
                    SearchType.OPIS_ID -> WHERE_OPIS_ID
                    SearchType.SYSTEM_ID -> WHERE_SYSTEM_ID
                },
                when (sort.fieldForSort) {
                    null -> SHIFR_SEARCH
                    SHIFR_VALUE -> SHIFR_SEARCH
                    else -> sort.fieldForSort
                }
            )

        return jdbcTemplate.query(
            selectWithSort,
            { resultSet: ResultSet, _: Int -> mapStorageUnitEntity(resultSet) },
            id, page.rowsPerPage, page.offset
        )
    }

    private fun mapStorageUnitEntity(resultSet: ResultSet): StorageUnitEntity =
        StorageUnitEntity(
            id = resultSet.getLong(DEAL_ID_COLUMN),
            number = resultSet.getString(DEAL_NUM_D_COLUMN),
            displayNumber = resultSet.getString(DEAL_DISPLAY_NUM_D_COLUMN),
            title = resultSet.getString(DEAL_TITLE_COLUMN),
            dates = resultSet.getString(DEAL_DATE_SRC_COLUMN),
            pages = resultSet.getString(DEAL_PAGES_COLUMN)
        )

    fun countStorageUnits(fondId: Long, opisId: Long?, systemId: Long?): Long {
        return jdbcTemplate.queryForObject(
            String.format(
                SELECT_COUNT,
                when {
                    systemId != null -> WHERE_SYSTEM_ID
                    opisId != null -> WHERE_OPIS_ID
                    else -> WHERE_FOND_ID
                }
            ), Long::class.java, systemId ?: opisId ?: fondId
        )
    }

    fun countStorageUnits(searchForm: SearchForm): Long {
        return jdbcTemplate.queryForObject(
            String.format(
                SELECT_COUNT_SEARCH,
                SearchUtils.createWhereSectionDeals(searchForm),
            ), Long::class.java
        ) ?: 0L
    }

    fun find(dealId: Long): StorageUnitEntity? =
        jdbcTemplate.query(
            DEAL_BY_ID,
            ResultSetExtractor {
                if (it.next()) {
                    StorageUnitEntity(
                        id = it.getLong("id"),
                        title = it.getString("title"),
                        pages = it.getString("pages"),
                        displayNumber = it.getString("disp_num_d"),
                        num_d = it.getString("num_d"),
                        num_o = it.getString("num_o"),
                        num_f = it.getString("num_f"),
                        suffix = it.getString("suffix"),
                        suffix_o = it.getString("suffix_o")
                    )
                } else null
            },
            dealId
        )

    companion object {

        const val DEAL_ID_COLUMN = "id"
        const val DEAL_NUM_D_COLUMN = "num_d"
        const val DEAL_DISPLAY_NUM_D_COLUMN = "disp_num_d"
        const val DEAL_TITLE_COLUMN = "title"
        const val DEAL_DATE_SRC_COLUMN = "dates_src"
        const val DEAL_START_DATE_COLUMN = "start_date"
        const val DEAL_PAGES_COLUMN = "pages"
        const val DEAL_SYSTEM_PARTITION_NAME_COLUMN = "system_partition_name"
        const val DEAL_FOND_ID_COLUMN = "fond_id"

        const val DEAL_BY_ID =
            """
                select id,
                       title, 
                       pages,
                       disp_num_d,
                       num_d,
                       num_o,
                       num_f,
                       suffix,
                       suffix_o
                from v_deal_site
                where id = ?
            """

        const val DEAL_SEARCH =
            """
                select DS.id,
                       DS.title, 
                       DS.pages,
                       D.important_category_id, 
                       D.barcode 
                from public.deal D inner join v_deal_site DS
                   on D.id = DS.id
                where DS.num_f = ?
                  and DS.num_o = ?
                  and DS.num_d = ?
            """

        const val DEAL_COPY =
            """
       			select 1
                from public.deal_copy
                where deal_id = ?
                  and copy_type_id = 3
            """

        const val SELECT_ONE =
            """
           select DS.id
                 ,DS.num_d
                 ,DS.disp_num_d
                 ,DS.title
                 ,DS.dates_src
                 ,DS.pages
                 ,DP.full_name system_partition_name
                 ,DP.system_partition_id
                 ,DS.fond_id
                 ,DS.annotation
                 ,I.deal_person
                 ,I.deal_person_author
                 ,I.deal_place
                 ,I.deal_theme
                 ,I.deal_system_partition
                 ,I.deal_material_type
                 ,DP.opis_id
            from v_deal_site DS left outer join v_deal_system_partition DP
                    on DS.id = DP.deal_id 
                left join v_deal_info_c I
                    on I.id = DS.id 
            where DS.id = ?   
        """

        const val SELECT_COUNT = """
                select count(*) 
                from v_deal_site vd
                where %s
            """
        const val SELECT_ALL = """
                select vd.id 
                      ,vd.num_d
                      ,vd.disp_num_d
                      ,vd.title
                      ,vd.dates_src
                      ,vd.pages
                from v_deal_site vd
                where %s
                order by vd.%s  
                limit ?
                offset ?
           """

        const val SELECT_ALL_SEARCH = """
                select vd.id 
                      ,vd.num_d
                      ,vd.disp_num_d
                      ,vd.title
                      ,vd.dates_src
                      ,vd.pages
                from v_deal_site vd
                where vd.id in (
                    select deal_id 
                    from v_deal_search
                    where 1 = 1 
                    %s
                )
                order by vd.%s  
                limit ?
                offset ?
           """

        const val SELECT_COUNT_SEARCH = """
                select count(*) 
                from v_deal_site vd
                where vd.id in (
                    select deal_id 
                    from v_deal_search
                    where 1 = 1 
                    %s
                )
            """

        const val WHERE_FOND_ID = """
              vd.fond_id = ?
           """

        const val WHERE_OPIS_ID = """
              vd.opis_id = ?
           """

        const val WHERE_SYSTEM_ID = """
              vd.partition_id = ?
           """

        const val SHIFR_SEARCH = "num_f, num_o, coalesce(suffix_o,'-1'), num_d, coalesce(suffix,'-1')"

        private enum class SearchType {
            OPIS_ID, FOND_ID, SYSTEM_ID
        }
    }
}