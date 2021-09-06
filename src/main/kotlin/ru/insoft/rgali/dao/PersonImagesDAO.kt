package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.insoft.rgali.entity.ImageHeaderEntity
import java.sql.ResultSet

@Repository
class PersonImagesDAO(private val jdbcTemplate: JdbcTemplate) {

    fun getHeaderPersonImages(offset: Int): List<ImageHeaderEntity> =
        jdbcTemplate.query(
            SELECT_ALL,
            { resultSet: ResultSet, _: Int ->
                ImageHeaderEntity(
                    imageId = resultSet.getLong("image_id"),
                    personId = resultSet.getLong("person_id"),
                    fio = resultSet.getString("fio"),
                    fondId = resultSet.getLong("fond_id"),
                    fnumFull = resultSet.getString("fnum_full"),
                    fullFileName = resultSet.getString("full_file_name")
                )
            },
            offset
        )

    fun countPersonImages(): Long = jdbcTemplate.queryForObject(SELECT_COUNT, Long::class.java)!!

    companion object {
        private const val SELECT_ALL =
            """
                select distinct 
                        image_id, 
                        person_id, 
                        fio, 
                        fond_id, 
                        fnum_full, 
                        full_file_name 
                from site.v_fond_person_image
                limit 6
                offset ?
            """

        private const val SELECT_COUNT =
            """
                select count(distinct person_id) - 6 max_offset
                from site.v_fond_person_image
            """
    }
}