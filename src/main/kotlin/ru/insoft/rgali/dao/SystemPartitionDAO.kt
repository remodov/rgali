package ru.insoft.rgali.dao

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import ru.insoft.rgali.entity.SystemPartitionEntity

@Repository
class SystemPartitionDAO(
    private val jdbcTemplate: JdbcTemplate
) {
    fun getOpisSystemPartition(systemPartitionId: Long): SystemPartitionEntity? {
        return jdbcTemplate.query(
            SELECT_ONE,
            ResultSetExtractor { rs ->
                rs.next()
                SystemPartitionEntity(
                    id = rs.getLong("id"),
                    name = rs.getString("name")
                )
            }, systemPartitionId
        )
    }

    companion object {

        const val SELECT_ONE =
            """
             select id, name
             from v_opis_system_partition
             where id = ?
            """
    }
}