package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.insoft.rgali.entity.SystematizationEntity

@Repository
class SystematizationDAO(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getSystematizationByOpisId(opisId: Long?): List<SystematizationEntity>? {
        return jdbcTemplate.query(
            SELECT_ONE,
            { resultSet, _ ->
                SystematizationEntity(
                    id = resultSet.getLong(SYSTEMATIZATION_ID_COLUMN),
                    parentId = resultSet.getLong(SYSTEMATIZATION_PARENT_ID_COLUMN),
                    fullName = resultSet.getString(SYSTEMATIZATION_FULL_NAME_COLUMN),
                    level = resultSet.getInt(SYSTEMATIZATION_LEVEL_COLUMN),
                    dealAmountTotal = resultSet.getInt(SYSTEMATIZATION_DEAL_AMOUNT_TOTAL_COLUMN)
                )
            },
            opisId
        )
    }

    companion object {

        private const val SYSTEMATIZATION_ID_COLUMN = "id"
        private const val SYSTEMATIZATION_PARENT_ID_COLUMN = "parent_id"
        private const val SYSTEMATIZATION_FULL_NAME_COLUMN = "full_name"
        private const val SYSTEMATIZATION_LEVEL_COLUMN = "level"
        private const val SYSTEMATIZATION_DEAL_AMOUNT_TOTAL_COLUMN = "deal_amount_total"

        private const val SELECT_ONE = """ 
            WITH RECURSIVE r AS (
               SELECT id, parent_id, name, 1 AS level, name as full_name, opis_id
               FROM v_opis_system_partition
               WHERE parent_id is null 
                 and opis_id = ? 
               UNION ALL
               SELECT ch.id, ch.parent_id, ch.name, r.level + 1 AS level, r.name || '\'||  ch.name as full_name, r.opis_id
               FROM v_opis_system_partition ch 
               JOIN r ON ch.parent_id = r.id
            )
            select r.*, 
            (select count(*) 
               from v_deal_system_partition dsp 
              where dsp.opis_id = r.opis_id 
                and dsp.system_partition_id = r.id ) as deal_amount, 
            ( WITH RECURSIVE rd AS (
               SELECT id
               FROM v_opis_system_partition
               WHERE id = r.id  
               UNION ALL
               SELECT chrd.id
               FROM v_opis_system_partition chrd 
               JOIN rd ON chrd.parent_id = rd.id)
               select count(*) 
               from site.v_deal_system_partition dsp join rd 
                    on dsp.system_partition_id = rd.id 
                   and dsp.opis_id = r.opis_id) as deal_amount_total   
              from r
            """
    }
}