package ru.insoft.rgali.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.dao.AskSiteDAO
import ru.insoft.rgali.dao.NewsDAO
import ru.insoft.rgali.dao.PersonImagesDAO
import ru.insoft.rgali.dao.SearchDAO
import ru.insoft.rgali.dao.StorageUnitDAO.Companion.DEAL_NUM_D_COLUMN
import ru.insoft.rgali.dao.StorageUnitDAO.Companion.DEAL_PAGES_COLUMN
import ru.insoft.rgali.dao.StorageUnitDAO.Companion.DEAL_START_DATE_COLUMN
import ru.insoft.rgali.dao.StorageUnitDAO.Companion.DEAL_TITLE_COLUMN
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort
import ru.insoft.rgali.model.html.Option
import ru.insoft.rgali.model.html.Select
import ru.insoft.rgali.service.StorageUnitService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/storage-unit")
class StorageUnitController(
    private val storageUnitService: StorageUnitService,
    private val searchDAO: SearchDAO,
    override val newsDAO: NewsDAO,
    override val askSiteDAO: AskSiteDAO,
    override val personImagesDAO: PersonImagesDAO
) : CommonController(personImagesDAO, askSiteDAO, newsDAO) {

    @GetMapping
    fun storageUnitList(
        @RequestParam fundId: Long,
        @RequestParam opisId: Long? = null,
        @RequestParam systemId: Long? = null,
        @RequestParam(defaultValue = "1") currentPage: Long,
        @RequestParam(defaultValue = "20") elementsOnPage: Long,
        @RequestParam(defaultValue = SHIFR_VALUE) fieldForSort: String?,
        @RequestParam(defaultValue = "1") viewType: String?,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model
    ): String {

        model.addAttribute(
            MODEL, storageUnitService.getStorageListModel(
                systemId = systemId,
                opisId = opisId,
                fondId = fundId,
                page = Page(currentPage = currentPage, rowsPerPage = elementsOnPage),
                sort = Sort(fieldForSort = fieldForSort)
            )
        )

        val viewType = when {
            systemId != null -> "3"
            opisId != null -> "2"
            else -> "1"
        }

        model.addAttribute("selectPage", getSelectPage(elementsOnPage.toString()))
        model.addAttribute("selectWithSort", getSelectWithSort(fieldForSort))
        model.addAttribute("currentFundId", fundId)
        model.addAttribute("currentOpisId", opisId)
        model.addAttribute("currentSystemId", systemId)
        model.addAttribute("viewType", viewType)
        model.addAttribute("isSearch", false)

        addCommonModels(httpRequest, httpResponse, model)

        return "pages/storage/storage-unit-list"
    }

    private fun getSelectWithSort(fieldForSort: String?): Select {
        val select = Select()
        select.options.add(Option(value = DEAL_NUM_D_COLUMN, text = "Номер ед.хр.", selected = false))
        select.options.add(Option(value = SHIFR_VALUE, text = "Шифр", selected = false))
        select.options.add(Option(value = DEAL_TITLE_COLUMN, text = "Заголовок ед.хр.", selected = false))
        select.options.add(Option(value = DEAL_START_DATE_COLUMN, text = "Крайние даты", selected = false))
        select.options.add(Option(value = DEAL_PAGES_COLUMN, text = "Кол-во листов", selected = false))
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

    @GetMapping("/{id}")
    fun storageUnitCard(@PathVariable id: Long,
                        httpRequest: HttpServletRequest,
                        httpResponse: HttpServletResponse,
                        model: Model): String {
        model.addAttribute(MODEL, storageUnitService.getStorageUnitModel(id))

        addCommonModels(httpRequest, httpResponse, model)

        return "pages/storage/storage-unit-card"
    }

    @GetMapping("/search")
    fun storageUnitSearch(@ModelAttribute searchForm: SearchForm,
                          httpRequest: HttpServletRequest,
                          httpResponse: HttpServletResponse,
                          model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        model.addAttribute(
            MODEL, storageUnitService.getStorageSearchModel(
                searchForm,
                page = Page(currentPage = searchForm.currentPage, rowsPerPage = searchForm.elementsOnPage),
                sort = Sort(
                    fieldForSort =
                    if (searchForm.fieldForSort == null || (searchForm.fieldForSort?.isBlank() == true)) SHIFR_VALUE
                    else searchForm.fieldForSort
                )
            )
        )

        model.addAttribute("selectPage", getSelectPage(searchForm.elementsOnPage.toString()))
        model.addAttribute(
            "selectWithSort", getSelectWithSort(
                if (searchForm.fieldForSort == null || (searchForm.fieldForSort?.isBlank() == true)) SHIFR_VALUE
                else searchForm.fieldForSort
            )
        )
        model.addAttribute("viewType", "4")
        model.addAttribute("searchForm", searchForm)
        model.addAttribute("isSearch", true)
        model.addAttribute("searchResultCounts", searchDAO.getSearchResults(searchForm))

        return "pages/storage/storage-unit-list"
    }

    companion object {
        const val MODEL = "model"
        const val SHIFR_VALUE = "shifr"
    }
}