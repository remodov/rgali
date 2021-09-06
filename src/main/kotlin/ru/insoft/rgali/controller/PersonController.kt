package ru.insoft.rgali.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.dao.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/person")
class PersonController(
    private val personDAO: PersonDAO,
    private val searchDAO: SearchDAO,
    override val newsDAO: NewsDAO,
    override val askSiteDAO: AskSiteDAO,
    override val personImagesDAO: PersonImagesDAO
) : CommonController(personImagesDAO, askSiteDAO, newsDAO) {

    @GetMapping("/{id}")
    fun organizationCard(@PathVariable id: Long,
                         httpRequest: HttpServletRequest,
                         httpResponse: HttpServletResponse,
                         model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        val personById = personDAO.getPersonById(id)
        val searchForm = SearchForm(personaName = personById?.fio)

        model.addAttribute("searchForm", searchForm)
        model.addAttribute("model", personById)
        model.addAttribute("searchResultCounts", searchDAO.getSearchResults(searchForm, id))

        return "pages/person/person-card"
    }

    @GetMapping("/search")
    fun personSearch(
        @ModelAttribute searchForm: SearchForm,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute("isSearch", true)
        model.addAttribute("searchForm", searchForm)
        model.addAttribute("model", personDAO.getPersonsByFio(searchForm.personaName))
        model.addAttribute("searchResultCounts", searchDAO.getSearchResults(searchForm))

        return "pages/person/person-list-table"
    }
}