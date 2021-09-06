package ru.insoft.rgali.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.dao.AskSiteDAO
import ru.insoft.rgali.dao.NewsDAO
import ru.insoft.rgali.dao.PersonImagesDAO
import ru.insoft.rgali.dao.SearchDAO
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort
import ru.insoft.rgali.model.html.Option
import ru.insoft.rgali.model.html.Select
import ru.insoft.rgali.service.FundService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping("/fund")
class FundController(
    private val fundService: FundService,
    private val searchDAO: SearchDAO,
    override val newsDAO: NewsDAO,
    override val askSiteDAO: AskSiteDAO,
    override val personImagesDAO: PersonImagesDAO
) : CommonController(personImagesDAO, askSiteDAO, newsDAO) {

    @GetMapping
    fun fundList(
        @RequestParam(defaultValue = "all") fundType: String?,
        @RequestParam(defaultValue = "1") currentPage: Long?,
        @RequestParam(defaultValue = "20") elementsOnPage: Long,
        @RequestParam(defaultValue = "num_f") fieldForSort: String?,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        val page = Page()
        page.currentPage = currentPage!!
        page.rowsPerPage = elementsOnPage
        val sort = Sort()
        sort.fieldForSort = fieldForSort
        model.addAttribute("model", fundService.getFundsModel(fundType, sort, page))
        model.addAttribute("selectPage", getSelectPage(elementsOnPage.toString()))
        model.addAttribute("selectWithSort", getSelectWithSort(fieldForSort))
        model.addAttribute("fundType", fundType)
        model.addAttribute("isSearch", false)
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/fund/fund-list-table"
    }

    private fun getSelectWithSort(fieldForSort: String?): Select {
        val select = Select()
        select.options.add(Option(value = "num_f", text = "номер фонда", selected = false))
        select.options.add(Option(value = "fond_name", text = "название фонда", selected = false))
        select.options.add(Option(value = "edge_start", text = "крайние даты", selected = false))
        select.options.add(Option(value = "units", text = "кол-во единиц хранения", selected = false))
        select.setSelectedField(fieldForSort)
        return select
    }

    fun getSelectPage(selectedPage: String): Select {
        val select = Select()
        select.options.add(Option(value = "20", text = "20", selected = false))
        select.options.add(Option(value = "50", text = "50", selected = false))
        select.options.add(Option(value = "100", text = "100", selected = false))
        select.setSelectedField(selectedPage)
        return select
    }

    @GetMapping("/{id}")
    fun fundCard(@PathVariable id: Long,
                 httpRequest: HttpServletRequest,
                 httpResponse: HttpServletResponse,
                 model: Model): String {
        model.addAttribute("model", fundService.getFundModel(id))

        addCommonModels(httpRequest, httpResponse, model)

        return "pages/fund/fund-card"
    }

    @GetMapping("/search")
    fun fundSearchList(
        @ModelAttribute searchForm: SearchForm,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {
        addCommonModels(httpRequest, httpResponse, model)

        val page = Page()
        page.currentPage = searchForm.currentPage
        page.rowsPerPage = searchForm.elementsOnPage
        val sort = Sort()
        sort.fieldForSort = if (searchForm.fieldForSort?.isBlank() == true) "num_f" else searchForm.fieldForSort

        model.addAttribute("searchForm", searchForm)
        model.addAttribute("searchResultCounts", searchDAO.getSearchResults(searchForm))
        model.addAttribute("model", fundService.getFundsModel("all", sort, page, searchForm))
        model.addAttribute("isSearch", true)
        model.addAttribute("selectPage", getSelectPage(searchForm.elementsOnPage.toString()))
        model.addAttribute("selectWithSort", getSelectWithSort(sort.fieldForSort))

        model.addAttribute("fundType", "all")

        return "pages/fund/fund-list-table"
    }
}