package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.insoft.rgali.controller.RegistrationForm
import ru.insoft.rgali.entity.DocumentGroupEntity
import java.sql.ResultSet

@Repository
class DocumentGroupDAO(
    private val jdbcTemplate: JdbcTemplate
) {
    fun findByFondId(fondId: Long): List<DocumentGroupEntity> =
        jdbcTemplate.query(
            SELECT_BY_FOND,
            { it: ResultSet, _ ->
                DocumentGroupEntity(
                    id = it.getLong("id"),
                    name = it.getString("h_groupname")
                )
            },
            fondId
        )

    companion object {
        private const val SELECT_BY_FOND = """
            select id, h_groupname
            from v_fond_document_group
            where fond_id = ?
        """
    }
}