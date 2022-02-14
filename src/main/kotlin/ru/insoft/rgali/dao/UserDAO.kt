package ru.insoft.rgali.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.insoft.rgali.controller.RegistrationForm

@Repository
class UserDAO(
    private val jdbcTemplate: JdbcTemplate,
    private val themeDAO: ResearchThemeDAO
) {
    fun findByEmail(email: String): RegistrationForm? =
        jdbcTemplate.query(
            SELECT_BY_EMAIL,
            ResultSetExtractor {
                if (it.next()) {
                    RegistrationForm(
                        id = it.getLong("id"),
                        firstName = it.getString("first_name"),
                        secondName = it.getString("second_name"),
                        thirdName = it.getString("last_name"),
                        password = it.getString("passw"),
                        passwordConfirm = it.getString("passw"),
                        isActive = it.getBoolean("is_activity"),
                        email = it.getString("email"),
                        confirmCode = it.getString("note"),
                        phone = it.getString("phone"),
                        index = it.getString("home_address"),
                        goal = it.getString("purpose_research_type_id"),
                        workTheme = it.getString("theme"),
                        organizationName = it.getString("company_name"),
                        billingDetails = it.getString("company_bank"),
                        country = it.getString("country"),
                        fioBoss = it.getString("company_chief"),
                        organizationIndexAndPhone = it.getString("company_address"),
                    )
                } else null
            },
            email
        )

    fun findByCode(code: String): RegistrationForm? =
        jdbcTemplate.query(
            SELECT_BY_CODE,
            ResultSetExtractor {
                it.next()
                RegistrationForm(
                    id = it.getLong("id"),
                    firstName = it.getString("first_name"),
                    secondName = it.getString("second_name"),
                    thirdName = it.getString("last_name"),
                    password = it.getString("passw"),
                    isActive = it.getBoolean("is_activity"),
                    email = it.getString("email"),
                    confirmCode = it.getString("note")
                )
            },
            code
        )

    fun registerUser(registrationForm: RegistrationForm) {
        registrationForm.password = BCryptPasswordEncoder().encode(registrationForm.password)

        with(registrationForm) {
            if (workTheme != null && workTheme!!.contains(Regex("\\d+"))) {
                workTheme = themeDAO.find(workTheme!!.toLong())?.text
            }
            jdbcTemplate.update(
                INSERT,
                firstName, secondName, thirdName, "$firstName $secondName $thirdName",
                index, phone, organizationIndexAndPhone,
                organizationName, fioBoss, organizationIndexAndPhone,
                billingDetails, country, email, password, workTheme, confirmCode,
                goal?.toLong()
            )
        }
    }

    fun updatePassword(email: String, newPassword: String) =
        jdbcTemplate.update(
            UPDATE_CHANGE_PASSWORD,
            BCryptPasswordEncoder().encode(newPassword), email)

    fun updateUser(registrationForm: RegistrationForm) =
        with(registrationForm) {
            jdbcTemplate.update(
                UPDATE,
                firstName, secondName, thirdName, "$firstName $secondName $thirdName",
                phone, organizationIndexAndPhone, organizationName, fioBoss, organizationIndexAndPhone,
                billingDetails, country, workTheme, goal?.toLong(), email
            )
        }

    @Transactional
    fun activateUser(confirmCode: String) {
        val user = findByCode(confirmCode)!!

        jdbcTemplate.update(
            UPDATE_ACTIVE, user.email
        )

        jdbcTemplate.update(
            INSERT_MAIN, user.id
        )
    }

    companion object {
        private const val UPDATE_ACTIVE = """
            update researcher_site
            set is_activity = true
            where email = ?    
        """

        private const val UPDATE_CHANGE_PASSWORD = """
            update public.researcher
            set passw = ?
            where email = ?    
        """

        private const val SELECT_BY_EMAIL = """
            select id, first_name, second_name, last_name, fio, 
                birth_date, home_address, phone, work_address, 
                company_name, company_chief, company_address, 
                company_bank, passport, country, email, passw, 
                open_date, is_remove_user, is_activity, is_unlimited_cases, 
                is_original, is_confirmed, theme, note, id_src_researcher, 
                purpose_research_type_id, id_src_purpose_research_type
            from public.researcher
            where email = ?    
        """

        private const val SELECT_BY_CODE = """
            select id, first_name, second_name, last_name, fio, 
                birth_date, home_address, phone, work_address, 
                company_name, company_chief, company_address, 
                company_bank, passport, country, email, passw, 
                open_date, is_remove_user, is_activity, is_unlimited_cases, 
                is_original, is_confirmed, theme, note, id_src_researcher, 
                purpose_research_type_id, id_src_purpose_research_type
            from researcher_site
            where note = ?    
        """

        private const val INSERT = """
            insert into researcher_site(
                id, first_name, second_name, last_name, fio, 
                birth_date, home_address, phone, work_address, 
                company_name, company_chief, company_address, 
                company_bank, passport, country, email, passw, 
                open_date, is_remove_user, is_activity, is_unlimited_cases, 
                is_original, is_confirmed, theme, note, id_src_researcher, 
                purpose_research_type_id, id_src_purpose_research_type)
            values(
                nextval('public.researcher_id_seq'),
                ?, ?, ?, ?,
                null, ?, ?, ?, 
                ?, ?, ?, 
                ?, null, ?, ?, ?, 
                current_date, false, false, false, 
                false, false, ?, ?, null, 
                ?, null
            )
        """

        private const val UPDATE = """
           update public.researcher 
           set first_name = ?,
               second_name = ?, 
               last_name = ?, 
               fio = ?, 
               phone = ?, 
               work_address = ?, 
               company_name = ?,
               company_chief = ?, 
               company_address = ?,
               company_bank = ?,
               country = ?, 
               theme = ?, 
               purpose_research_type_id = ?
           where email = ?      
        """

        private const val INSERT_MAIN = """
            insert into public.researcher(
                first_name, second_name, last_name, fio, 
                birth_date, home_address, phone, work_address, 
                company_name, company_chief, company_address, 
                company_bank, passport, country, email, passw, 
                open_date, is_remove_user, is_activity, is_unlimited_cases, 
                is_original, is_confirmed, theme, note, id_src_researcher, 
                purpose_research_type_id, id_src_purpose_research_type)
            select first_name, second_name, last_name, fio, 
                   birth_date, home_address, phone, work_address, 
                   company_name, company_chief, company_address, 
                   company_bank, passport, country, email, passw, 
                   open_date, is_remove_user, is_activity, is_unlimited_cases, 
                   is_original, is_confirmed, theme, null note, id_src_researcher, 
                   purpose_research_type_id, id_src_purpose_research_type
            from researcher_site
            where id = ?
        """

    }
}