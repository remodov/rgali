package ru.insoft.rgali.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.controller.PasswordGenerator.PasswordGeneratorBuilder
import ru.insoft.rgali.dao.AskSiteDAO
import ru.insoft.rgali.dao.NewsDAO
import ru.insoft.rgali.dao.PersonImagesDAO
import ru.insoft.rgali.dao.UserDAO
import ru.insoft.rgali.service.EmailService
import java.security.Principal
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
class SecurityController(
    val userDAO: UserDAO,
    val emailService: EmailService,
    override val newsDAO: NewsDAO,
    override val askSiteDAO: AskSiteDAO,
    override val personImagesDAO: PersonImagesDAO
) : CommonController(personImagesDAO, askSiteDAO, newsDAO) {

    @GetMapping("/login")
    fun login(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model,
        @RequestParam error: Boolean?
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute("hasError", error)

        return "pages/auth/login"
    }

    @GetMapping("/registration")
    fun registration(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute("registrationForm", RegistrationForm())

        return "pages/auth/registration"
    }

    @PostMapping("/registration")
    fun registrationStart(
        @ModelAttribute registrationForm: RegistrationForm, httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        val findByEmail = userDAO.findByEmail(registrationForm.email!!)

        if (findByEmail != null) {
            model.addAttribute(
                "infoMessage",
                "Вы уже были зарегистрированы на сайте. Перейдите на экран авторизации по кнопке «Вход»"
            )
            return "pages/auth/registration"
        }

        if (registrationForm.password != registrationForm.passwordConfirm) {
            model.addAttribute("infoMessage", "Пароли не совпадают!")
            return "pages/auth/registration"
        }

        val confirmCode = UUID.randomUUID().toString()

        registrationForm.confirmCode = confirmCode

        userDAO.registerUser(registrationForm)
        emailService.sendRegistrationEmail(registrationForm.email!!, confirmCode)

        model.addAttribute("infoMessage", "Вы успешно зарегистрированы! Подтвердите Вашу почту!")
        return "pages/auth/login"
    }

    @GetMapping("/reset-password")
    fun resetPassword(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute("resetPasswordForm", ResetPasswordForm())
        return "pages/auth/reset-password"
    }

    @PostMapping("/reset-password")
    fun startResetPasswordResetPasswordForm(
        @ModelAttribute resetPasswordForm: ResetPasswordForm,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        val findByEmail = userDAO.findByEmail(email = resetPasswordForm.email!!)
        if (findByEmail == null) {
            model.addAttribute("infoMessage", "Почта не найдена")
            return "pages/auth/login"
        }

        val passwordGenerator = PasswordGeneratorBuilder()
            .useDigits(true)
            .useLower(true)
            .useUpper(true)
            .build()
        val newPassword: String = passwordGenerator.generate(8)

        userDAO.updatePassword(resetPasswordForm.email!!, newPassword)
        emailService.sendResetPasswordEmail(resetPasswordForm.email!!, newPassword)

        model.addAttribute("infoMessage", "Новый пароль отправлен Вам на почту!")
        return "pages/auth/login"
    }

    @GetMapping("/confirm/{uniqueId}")
    fun confirm(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model,
        @PathVariable uniqueId: String
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        userDAO.activateUser(uniqueId)

        model.addAttribute("infoMessage", "Вы успешно подтвердили почту, аккаунт активирован!")
        return "pages/auth/login"
    }

    @RequestMapping(value = ["/username"], method = [RequestMethod.GET])
    @ResponseBody
    fun currentUserName(principal: Principal): String? {
        return principal.name
    }
}

data class RegistrationForm(
    var id: Long? = null,
    var firstName: String? = null,
    var secondName: String? = null,
    var thirdName: String? = null,
    var country: String? = "Россия",
    var phone: String? = null,
    var email: String? = null,
    var index: String? = null,
    var goal: String? = null,
    var workTheme: String? = null,
    var password: String? = null,
    var passwordConfirm: String? = null,
    var organizationName: String? = null,
    var fioBoss: String? = null,
    var organizationIndexAndPhone: String? = null,
    var billingDetails: String? = null,
    var confirmPersonalData: Boolean = true,
    var isActive: Boolean = false,
    var confirmCode: String? = null
)

data class ResetPasswordForm(
    var email: String? = null,
)

class PasswordGenerator {
    private var useLower = false
    private var useUpper = false
    private var useDigits = false
    private var usePunctuation = false

    private constructor() {
        throw UnsupportedOperationException("Empty constructor is not supported.")
    }

    private constructor(builder: PasswordGeneratorBuilder) {
        useLower = builder.useLower
        useUpper = builder.useUpper
        useDigits = builder.useDigits
        usePunctuation = builder.usePunctuation
    }

    class PasswordGeneratorBuilder {
        var useLower = false
        var useUpper = false
        var useDigits = false
        var usePunctuation = false

        fun useLower(useLower: Boolean): PasswordGeneratorBuilder {
            this.useLower = useLower
            return this
        }

        fun useUpper(useUpper: Boolean): PasswordGeneratorBuilder {
            this.useUpper = useUpper
            return this
        }

        fun useDigits(useDigits: Boolean): PasswordGeneratorBuilder {
            this.useDigits = useDigits
            return this
        }

        fun usePunctuation(usePunctuation: Boolean): PasswordGeneratorBuilder {
            this.usePunctuation = usePunctuation
            return this
        }

        fun build(): PasswordGenerator {
            return PasswordGenerator(this)
        }
    }

    fun generate(length: Int): String {
        // Argument Validation.
        if (length <= 0) {
            return ""
        }

        // Variables.
        val password = StringBuilder(length)
        val random = Random(System.nanoTime())

        // Collect the categories to use.
        val charCategories: MutableList<String> = ArrayList(4)
        if (useLower) {
            charCategories.add(LOWER)
        }
        if (useUpper) {
            charCategories.add(UPPER)
        }
        if (useDigits) {
            charCategories.add(DIGITS)
        }
        if (usePunctuation) {
            charCategories.add(PUNCTUATION)
        }

        // Build the password.
        for (i in 0 until length) {
            val charCategory = charCategories[random.nextInt(charCategories.size)]
            val position = random.nextInt(charCategory.length)
            password.append(charCategory[position])
        }
        return String(password)
    }

    companion object {
        private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
        private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val DIGITS = "0123456789"
        private const val PUNCTUATION = "!@#$%&*()_+-=[]|,./?><"
    }
}