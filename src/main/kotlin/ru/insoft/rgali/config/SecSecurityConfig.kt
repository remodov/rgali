package ru.insoft.rgali.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import ru.insoft.rgali.dao.UserDAO


@Configuration
@EnableWebSecurity
class SecSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var userDetailsService: MyUserDetailsService

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/**").permitAll()
            .antMatchers("/", "/login", "/registration", "/change/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/")
            .failureUrl("/login?error=true")
            .and()
            .logout()
            .logoutUrl("/logout")
            .deleteCookies("JSESSIONID")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

@Component
class MyUserDetailsService(private val userDAO: UserDAO) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userDAO.findByEmail(email) ?: throw UsernameNotFoundException("No user found with username: $email")
        val enabled = user.isActive
        val accountNonExpired = true
        val credentialsNonExpired = true
        val accountNonLocked = true

        return RgaliUser(
            id = user.id!!,
            email = user.email,
            fio = user.firstName + user.secondName,
            "${user.firstName} ${user.secondName} ${user.thirdName}",
            user.password, enabled, accountNonExpired,
            credentialsNonExpired, accountNonLocked, getAuthorities(listOf("USER"))
        )
    }

    fun getAuthorities(roles: List<String>): MutableList<GrantedAuthority>? {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        for (role in roles) {
            authorities.add(SimpleGrantedAuthority(role))
        }
        return authorities
    }

}

class RgaliUser(
    var id: Long,
    var email: String?,
    var fio: String?,
    username: String?,
    password: String?,
    enabled: Boolean,
    accountNonExpired: Boolean,
    credentialsNonExpired: Boolean,
    accountNonLocked: Boolean,
    authorities: MutableList<out GrantedAuthority>?
) : User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities)
