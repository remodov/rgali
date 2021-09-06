package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.insoft.rgali.entity.ImageExtensionsEntity
import ru.insoft.rgali.entity.ImageHeaderEntity
import java.sql.ResultSet

@Repository
class ImageExtensionsDAO(private val jdbcTemplate: JdbcTemplate) {

    fun getImageExtensions(): Map<String, List<String>> =
        jdbcTemplate.query(
            SELECT_ALL
        ) { resultSet: ResultSet, _: Int ->
            ImageExtensionsEntity(
                size = resultSet.getString("image_size"),
                extensions = resultSet.getString("image_ext").split(","),
            )
        }.associate { Pair(it.size, it.extensions) }

    companion object {
        private const val SELECT_ALL =
            """
                SELECT image_size, image_ext
                FROM public.image_ext 
            """
    }
}