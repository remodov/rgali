package ru.insoft.rgali.dao

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.insoft.rgali.entity.OpisEntity
import java.sql.ResultSet
import java.util.*

@Repository
class OpisDAO (
    private val jdbcTemplate: JdbcTemplate
) {

    fun getOpisesByFondId(fondId: Long): Optional<List<OpisEntity>> {
        return getOpises(fondId, true)
    }

    fun getOpis(id: Long): Optional<OpisEntity> {
        val opises = getOpises(id, false)
        return opises.map { opisEntities: List<OpisEntity> -> opisEntities[0] }
    }

    private fun getOpises(id: Long, isListNeeded: Boolean): Optional<List<OpisEntity>> {
        val result = jdbcTemplate.query(
            String.format(SELECT, if (isListNeeded) SELECT_LIST_WHERE else SELECT_ONE_WHERE),
            { resultSet: ResultSet, _: Int ->
                OpisEntity(
                    id = resultSet.getLong("id"),
                    units = resultSet.getLong("units"),
                    displayNum = resultSet.getString("full_num_o"),
                    displayFullNum = resultSet.getString("disp_num_o"),
                    dates = resultSet.getString("opis_dates"),
                    fondId = resultSet.getLong("fond_id")
                )
            },
            id
        )
        return Optional.ofNullable(result)
    }

    companion object {
        private const val SELECT = """
            select O.id,
                   O.disp_num_o, 
                   O.fond_id, 
                   O.full_num_o,
                   O.opis_dates,
                   O.units 
            from v_opis_site O
            where %s
            order by O.num_o, coalesce(O.suffix, '-1')
                """
        private const val SELECT_LIST_WHERE = " O.fond_id = ?"
        private const val SELECT_ONE_WHERE = "  O.id = ?"
    }
}