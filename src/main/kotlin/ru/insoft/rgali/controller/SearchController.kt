
package ru.insoft.rgali.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import ru.insoft.rgali.dao.AskSiteDAO
import ru.insoft.rgali.dao.NewsDAO
import ru.insoft.rgali.dao.PersonImagesDAO
import ru.insoft.rgali.dao.SearchDAO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping("/search")
class SearchController(
    private val searchDAO: SearchDAO,
    override val newsDAO: NewsDAO,
    override val askSiteDAO: AskSiteDAO,
    override val personImagesDAO: PersonImagesDAO
) : CommonController(personImagesDAO, askSiteDAO, newsDAO) {

    @GetMapping
    fun search(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/search/search-form"
    }

    @GetMapping("/start")
    fun searchResults(
        @ModelAttribute searchForm: SearchForm,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute("searchResultCounts", searchDAO.getSearchResults(searchForm))
        if (searchForm.organizationName == null) {
            searchForm.organizationName = ""
        }

        if (searchForm.personaName == null) {
            searchForm.personaName = ""
        }

        model.addAttribute("searchForm", searchForm)

        return "pages/search/search-results"
    }
}

data class SearchForm(
    var fundNumber: String? = null,
    var opisNumber: String? = null,
    var storageUnitNumber: String? = null,
    var fondName: String? = null,
    var personaName: String? = null,
    var organizationName: String? = null,
    var yearDocumentFrom: String? = null,
    var yearDocumentTo: String? = null,
    var currentPage: Long = 1,
    var elementsOnPage: Long = 20,
    var fieldForSort: String? = null,
    var simpleSearch: Boolean = false,
    var personType: String? = null,
    var personId: String? = null,
    var fondType: String? = null,
)