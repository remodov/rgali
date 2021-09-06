package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.entity.*
import ru.insoft.rgali.utils.SearchUtils
import java.sql.ResultSet
import java.util.*

@Repository
class CatalogDAO(
    private val jdbcTemplate: JdbcTemplate
) {

    fun findAll(search: String?, sort: Sort, page: Page): List<CatalogEntity> {
        val selectWithSearch =
            if (search != null)
                SELECT_LIST.format(WHERE_SEARCH.format(search), if (sort.fieldForSort == null) "author" else sort.fieldForSort)
             else
                SELECT_LIST.format(" ", if (sort.fieldForSort == null) "author" else sort.fieldForSort)

        val result = jdbcTemplate.query(
            selectWithSearch,
            { resultSet: ResultSet, _: Int ->
                CatalogEntity(
                    invNumber = resultSet.getString("inv_number"),
                    name = resultSet.getString("name"),
                    author = resultSet.getString("author")
                )
            },
            page.rowsPerPage, page.offset
        )
        return result
    }

    fun count(search: String?): Long =
        if (search != null)
            jdbcTemplate.queryForObject(SELECT_COUNT.format(WHERE_SEARCH.format(search)), Long::class.java)!!
        else
            jdbcTemplate.queryForObject(SELECT_COUNT.format("and 1=1"), Long::class.java)!!

    companion object {

        private const val SELECT_LIST =
        """
            SELECT inv_number, 
                   lc.name, 
                   author
            FROM public.printed_publications_catalog ppc 
                    join public.liter_cls lc 
                  on ppc.id_src_liter = lc.id_src_liter
            where ppc.id_src_liter = '10902577'
              %s
            order by %s  
            limit ?
            offset ?
        """

        private const val SELECT_COUNT =
        """
            SELECT count(*)
            FROM public.printed_publications_catalog ppc 
                    join public.liter_cls lc 
                  on ppc.id_src_liter = lc.id_src_liter
            where ppc.id_src_liter = '10902577'
              %s
        """

        private const val WHERE_SEARCH =
            """
              and author ~*'%s'
            """
    }
}