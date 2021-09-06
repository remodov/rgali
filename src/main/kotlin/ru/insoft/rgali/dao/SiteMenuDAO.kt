package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository

@Repository
class SiteMenuDAO(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getMenuInfo(menuId: Long): MenuInfo? =
        jdbcTemplate.query(
            SELECT,
            ResultSetExtractor {
                if (it.next()) {
                    MenuInfo(
                        title = it.getString("title"),
                        iconPath = it.getString("icon_file_name")
                    )
                } else {
                    MenuInfo("", "")
                }
            },
            menuId
        )

    companion object {
        const val SELECT = """
            select title, icon_file_name 
            from public.site_menu 
            where id = ?
        """
    }
}

data class MenuInfo(
    var iconPath: String?,
    var title: String?
)