package ru.insoft.rgali.controller

import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.config.RgaliUser
import ru.insoft.rgali.dao.*
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.AskEntity
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort
import ru.insoft.rgali.model.html.Option
import ru.insoft.rgali.model.html.Select
import ru.insoft.rgali.service.AskService
import ru.insoft.rgali.service.DateService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping("/cabinet")
class CabinetController(
    val userDAO: UserDAO,
    val researchThemeDAO: ResearchThemeDAO,
    val purposeResearchDAO: PurposeResearchDAO,
    val askService: AskService,
    val askDAO: AskDAO,
    val storageUnitDAO: StorageUnitDAO,
    val dateService: DateService,
    override val newsDAO: NewsDAO,
    override val askSiteDAO: AskSiteDAO,
    override val personImagesDAO: PersonImagesDAO
) : CommonController(personImagesDAO, askSiteDAO, newsDAO) {

    @Secured("USER")
    @GetMapping("/user-info")
    fun getUserInfo(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model,
        authentication: Authentication
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        val rgaliUser = authentication.principal as RgaliUser

        val registrationForm = userDAO.findByEmail(rgaliUser.email!!)

        val researchTheme = if (registrationForm?.workTheme?.matches(Regex("\\d+")) == true) {
            registrationForm.workTheme?.let {
                researchThemeDAO.find(it.toLong())
            }
        } else {
            SelectDto(null, registrationForm?.workTheme)
        }

        val purpose = registrationForm?.goal?.let {
            purposeResearchDAO.find(it.toLong())
        }

        model.addAttribute("registrationForm", registrationForm)
        model.addAttribute(
            "goalSelectedOption",
            Option(value = purpose?.id ?: registrationForm?.goal, text = purpose?.text ?: registrationForm?.goal)
        )
        model.addAttribute(
            "workThemeSelectedOption",
            Option(value = registrationForm?.workTheme, text = researchTheme?.text)
        )

        return "pages/cabinet/user-info"
    }

    @Secured("USER")
    @PostMapping("/user-info")
    fun updateUserInfo(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model,
        @ModelAttribute registrationForm: RegistrationForm,
        authentication: Authentication
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute("registrationForm", registrationForm)
        val researchTheme = if (registrationForm.workTheme?.matches(Regex("\\d+")) == true) {
            registrationForm.workTheme?.let {
                researchThemeDAO.find(it.toLong())
            }
        } else {
            SelectDto(null, registrationForm.workTheme)
        }

        if (registrationForm.goal?.isEmpty() == true) {
            registrationForm.goal = null
        }

        val purpose = registrationForm.goal?.let {
            purposeResearchDAO.find(it.toLong())
        }

        model.addAttribute(
            "goalSelectedOption",
            Option(value = purpose?.id ?: registrationForm.goal, text = purpose?.text ?: registrationForm.goal)
        )
        model.addAttribute(
            "workThemeSelectedOption",
            Option(value = registrationForm.workTheme, text = researchTheme?.text)
        )

        registrationForm.password?.let {
            if (registrationForm.password != registrationForm.passwordConfirm) {
                model.addAttribute("infoMessage", "Пароли не совпадают!")
                return "pages/cabinet/user-info"
            }
            registrationForm.password = BCryptPasswordEncoder().encode(registrationForm.password)
        }

        userDAO.updateUser(registrationForm)

        model.addAttribute("infoMessage", "Данные успешно обновлены!")

        return "pages/cabinet/user-info"
    }

    @Secured("USER")
    @GetMapping("/change-password")
    fun getChangePassword(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute("changePasswordForm", ChangePasswordForm())

        return "pages/cabinet/change-password"
    }

    @Secured("USER")
    @PostMapping("/change-password")
    fun changePassword(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model,
        @ModelAttribute changePasswordForm: ChangePasswordForm,
        authentication: Authentication
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute("changePasswordForm", changePasswordForm)

        val rgaliUser = authentication.principal as RgaliUser

        if (changePasswordForm.password != changePasswordForm.passwordConfirm) {
            model.addAttribute("infoMessage", "Пароли не совпадают!")
            return "pages/cabinet/change-password"
        }

        userDAO.updatePassword(rgaliUser.email!!, changePasswordForm.password!!)

        model.addAttribute("infoMessage", "Данные успешно обновлены!")

        return "pages/cabinet/change-password"
    }

    @Secured("USER")
    @GetMapping("/requirements")
    fun askList(
        authentication: Authentication,
        @RequestParam(defaultValue = "1") currentPage: Long?,
        @RequestParam(defaultValue = "20") elementsOnPage: Long,
        @RequestParam(defaultValue = "ask_num") fieldForSort: String?,
        httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        val rgaliUser = authentication.principal as RgaliUser

        val page = Page()
        page.currentPage = currentPage!!
        page.rowsPerPage = elementsOnPage
        val sort = Sort()
        sort.fieldForSort = fieldForSort
        model.addAttribute("model", askService.getAsksModel(rgaliUser.id, sort, page))
        model.addAttribute("selectPage", getSelectPage(elementsOnPage.toString()))
        model.addAttribute("selectWithSort", getSelectWithSort(fieldForSort))

        return "pages/cabinet/requirements"
    }

    @Secured("USER")
    @GetMapping("/requirements/{askId}")
    fun ask(
        @PathVariable askId: Long,
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute("ask", askDAO.findOne(askId))
        model.addAttribute("askStates", askDAO.findAskStates(askId))

        return "pages/cabinet/requirement-info"
    }

    @Secured("USER")
    @GetMapping("/requirements-new")
    fun new(
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        val rgaliUser = authentication.principal as RgaliUser

        val findByEmail = userDAO.findByEmail(rgaliUser.email!!)

        val researchTheme = if (findByEmail?.workTheme?.matches(Regex("\\d+")) == true) {
            researchThemeDAO.find(findByEmail.workTheme!!.toLong())
        } else {
            null
        }

        val goal = if (findByEmail?.goal?.matches(Regex("\\d+")) == true) {
            purposeResearchDAO.find(findByEmail.goal!!.toLong())
        } else {
            null
        }

        model.addAttribute("researchTheme", researchTheme)
        model.addAttribute("goal", goal)


        model.addAttribute(
            "askForm", AskEntity(
                requestDate = LocalDate.now().format(dateFormat),
                askDate = dateService.getAskDate(LocalDate.now()).format(dateFormat),
                askNum = askDAO.getId(),
                researchThemeId = researchTheme?.id?.toLong(),
                purposeResearchTypeId = findByEmail?.goal?.toLong()
            )
        )

        return "pages/cabinet/requirement-new"
    }

    @Secured("USER")
    @GetMapping("/requirements-new/deal-id/{dealId}")
    fun newFromDealId(
        @PathVariable dealId: Long,
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        val storageUnit = storageUnitDAO.find(dealId)

        val rgaliUser = authentication.principal as RgaliUser

        val findByEmail = userDAO.findByEmail(rgaliUser.email!!)

        val researchTheme = if (findByEmail?.workTheme?.matches(Regex("\\d+")) == true) {
            researchThemeDAO.find(findByEmail.workTheme!!.toLong())
        } else {
            null
        }

        val goal = if (findByEmail?.goal?.matches(Regex("\\d+")) == true) {
            purposeResearchDAO.find(findByEmail.goal!!.toLong())
        } else {
            null
        }

        model.addAttribute("researchTheme", researchTheme)
        model.addAttribute("goal", goal)

        model.addAttribute(
            "askForm", AskEntity(
                requestDate = LocalDate.now().format(dateFormat),
                askDate = dateService.getAskDate(LocalDate.now()).format(dateFormat),
                askNum = askDAO.getId(),
                num_d = storageUnit?.num_d,
                num_f = storageUnit?.num_f,
                num_o = storageUnit?.num_o,
                headerDoc = storageUnit?.title,
                cypher = storageUnit?.displayNumber,
                numLists = storageUnit?.pages,
                literalOpis = storageUnit?.suffix_o,
                literalDeal = storageUnit?.suffix
            )
        )

        return "pages/cabinet/requirement-new"
    }


    @Secured("USER")
    @PostMapping("/requirements-new")
    fun newAsk(
        @ModelAttribute askEntity: AskEntity,
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        val rgaliUser = authentication.principal as RgaliUser

        askEntity.researcherId = rgaliUser.id

        askDAO.save(askEntity)

        val page = Page()
        page.currentPage = 1
        page.rowsPerPage = 20
        val sort = Sort()
        sort.fieldForSort = "ask_num"

        model.addAttribute("model", askService.getAsksModel(rgaliUser.id, sort, page))
        model.addAttribute("selectPage", getSelectPage(page.rowsPerPage.toString()))
        model.addAttribute("selectWithSort", getSelectWithSort(sort.fieldForSort))
        model.addAttribute("countInRecycle", askSiteDAO.countInRecycle(rgaliUser.id))

        return "pages/cabinet/requirements"
    }

    @Secured("USER")
    @GetMapping("/requirements-close/{askId}")
    fun close(
        @PathVariable askId: Long,
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        askDAO.closeAsk(askId)
        model.addAttribute("ask", askDAO.findOne(askId))
        model.addAttribute("askStates", askDAO.findAskStates(askId))

        return "pages/cabinet/requirement-info"
    }

    @Secured("USER")
    @GetMapping("/recycle")
    fun recycle(
        authentication: Authentication,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        val rgaliUser = authentication.principal as RgaliUser

        val asksSiteModel = askService.getAsksSiteModel(rgaliUser.id)
        model.addAttribute("asks", asksSiteModel)

        val findByEmail = userDAO.findByEmail(rgaliUser.email!!)

        val researchTheme = if (findByEmail?.workTheme?.matches(Regex("\\d+")) == true) {
            researchThemeDAO.find(findByEmail.workTheme!!.toLong())
        } else {
            null
        }

        val goal = if (findByEmail?.goal?.matches(Regex("\\d+")) == true) {
            purposeResearchDAO.find(findByEmail.goal!!.toLong())
        } else {
            null
        }

        model.addAttribute("researchTheme", researchTheme)
        model.addAttribute("goal", goal)

        model.addAttribute(
            "askForm", AskEntity(
                requestDate = LocalDate.now().format(dateFormat),
                askDate = dateService.getAskDate(LocalDate.now()).format(dateFormat)
            )
        )

        return "pages/cabinet/recycle"
    }

    private fun getSelectWithSort(fieldForSort: String?): Select {
        val select = Select()
        select.options.add(Option(value = "ask_num", text = "номер требования", selected = false))
        select.options.add(Option(value = "cypher", text = "Архивный шифр", selected = false))
        select.options.add(Option(value = "header_doc", text = "Заголовок ед.хр.", selected = false))
        select.options.add(Option(value = "ask_state_name", text = "Последнее состояние", selected = false))
        select.options.add(Option(value = "askdate", text = "Требуемая дата выдачи", selected = false))
        select.options.add(Option(value = "request_date", text = "Дата подачи требования", selected = false))
        select.setSelectedField(fieldForSort)
        return select
    }

    private fun getSelectPage(selectedPage: String): Select {
        val select = Select()
        select.options.add(Option(value = "20", text = "20", selected = false))
        select.options.add(Option(value = "50", text = "50", selected = false))
        select.options.add(Option(value = "100", text = "100", selected = false))
        select.setSelectedField(selectedPage)
        return select
    }

}

val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

data class ChangePasswordForm(
    var password: String? = null,
    var passwordConfirm: String? = null
)

data class RecycleForm(
    var requestDate: String? = null,
    var askDate: String? = null,
    var goalId: Long? = null,
    var themeId: String? = null,
    var askIds: List<Long>? = null,
)