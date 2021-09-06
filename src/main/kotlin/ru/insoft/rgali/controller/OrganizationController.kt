package ru.insoft.rgali.controller

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.config.RgaliUser
import ru.insoft.rgali.dao.*
import ru.insoft.rgali.entity.OrganizationEntity
import ru.insoft.rgali.service.OpisService
import ru.insoft.rgali.service.SystematizationService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/organization")
class OrganizationController(
    private val organizationDAO: OrganizationDAO,
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

        val organizationEntity = organizationDAO.getOrganizationById(id) ?: OrganizationEntity()
        val searchForm = SearchForm(
            organizationName = organizationEntity.orgName
        )

        model.addAttribute("searchForm", searchForm)
        model.addAttribute("model", organizationEntity)

        model.addAttribute("searchResultCounts", searchDAO.getSearchResults(searchForm))

        return "pages/organization/organization-card"
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
        model.addAttribute("model", organizationDAO.getOrganizationsByFondName(searchForm.organizationName!!))
        model.addAttribute("searchResultCounts", searchDAO.getSearchResults(searchForm))

        return "pages/organization/organization-list-table"
    }
}