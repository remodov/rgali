package ru.insoft.rgali.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.dao.*
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort
import ru.insoft.rgali.model.html.Option
import ru.insoft.rgali.model.html.Select
import ru.insoft.rgali.model.template.CatalogModel
import ru.insoft.rgali.model.template.TemplateModel
import ru.insoft.rgali.service.EmailService
import ru.insoft.rgali.service.NewsService
import ru.insoft.rgali.service.StaticPagesService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class StaticController(
    val newsService: NewsService,
    val staticPagesService: StaticPagesService,
    val catalogDAO: CatalogDAO,
    val emailService: EmailService,
    override val newsDAO: NewsDAO,
    override val askSiteDAO: AskSiteDAO,
    override val personImagesDAO: PersonImagesDAO
) : CommonController(personImagesDAO, askSiteDAO, newsDAO) {

    @GetMapping("/")
    fun index(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        model.addAttribute("news", newsService.allNews())
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/index"
    }

    @GetMapping("/rgali-anti-corruption")
    fun corruption(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)
        model.addAttribute("tourForm", TourForm())

        return "pages/rgali-anti-corruption"
    }

    @GetMapping("/archive-structure")
    fun structure(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/archive-structure"
    }

    @GetMapping("/contacts")
    fun contacts(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/contacts"
    }

    @GetMapping("/director")
    fun director(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/director"
    }

    @GetMapping("/request")
    fun request(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/request"
    }

    @GetMapping("/price")
    fun price(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/price"
    }

    @GetMapping("/normative-documents")
    fun normativeDocuments(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/normative-documents"
    }

    @GetMapping("/work-protection")
    fun workProtection(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/work-protection"
    }

    @GetMapping("/working")
    fun working(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/working"
    }

    @GetMapping("/history")
    fun history(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/history"
    }

    @GetMapping("/history-photos/{menuId}")
    fun historyPhotos(@PathVariable menuId: Long, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        val menuInfo = staticPagesService.getMenuItemInfo(menuId)
        val articlePages = staticPagesService.getArticles(menuId)

        val blocks: MutableList<Block> = mutableListOf()

        var currentBlock = Block()
        var index = 1
        articlePages?.forEach {
            currentBlock.articles.add(it!!)
            if (index % 3 == 0) {
                blocks.add(currentBlock)
                currentBlock = Block()
            }

            if (index != articlePages.size ) {
                index++
            }
        }

        if (index % 3 != 0) {
            blocks.add(currentBlock)
            if (currentBlock.articles.size != 3) {
                currentBlock.articles.add(Article(0,""))
            }
        }

        model.addAttribute("menuInfo", menuInfo)
        model.addAttribute("blocks", blocks)
        model.addAttribute("articles", articlePages)

        return "pages/history-photo"
    }

    data class Block(
        val articles: MutableList<Article> = mutableListOf()
    )

    @GetMapping("/history-photos")
    fun historyPhoto(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        val meropMenuItemInfo = staticPagesService.getMenuItemInfo(MEROP_MENU_ID)
        val dosugMenuItemInfo = staticPagesService.getMenuItemInfo(DOSUG_MENU_ID)
        val workDaysMenuItemInfo = staticPagesService.getMenuItemInfo(WORK_DAYS_MENU_ID)
        val gustFriendsMenuItemInfo = staticPagesService.getMenuItemInfo(GUST_FRIENDS_ARCHIVE_MENU_ID)

        model.addAttribute("meropMenuItemInfo", meropMenuItemInfo)
        model.addAttribute("dosugMenuItemInfo", dosugMenuItemInfo)
        model.addAttribute("workDaysMenuItemInfo", workDaysMenuItemInfo)
        model.addAttribute("gustFriendsMenuItemInfo", gustFriendsMenuItemInfo)

        return "pages/history-photos"
    }

    @GetMapping("/error")
    fun error(): String {
        return "pages/error"
    }

    @GetMapping("/books-autograph")
    fun booksAutograph(@RequestParam searchDocument: String?,
                       @RequestParam(defaultValue = "1") currentPage: Long?,
                       @RequestParam(defaultValue = "20") elementsOnPage: Long,
                       @RequestParam(defaultValue = "author") fieldForSort: String?,
                       httpRequest: HttpServletRequest,
                       httpResponse: HttpServletResponse,
                       model: Model): String
    {
        val page = Page()
        page.currentPage = currentPage!!
        page.rowsPerPage = elementsOnPage
        page.totalRows = catalogDAO.count(searchDocument)

        val sort = Sort()
        sort.fieldForSort = fieldForSort

        model.addAttribute("model", CatalogModel(
            pages = catalogDAO.findAll(searchDocument, sort, page), page = page, sort = sort )
        )
        model.addAttribute("selectPage", getSelectPage(elementsOnPage.toString()))
        model.addAttribute("selectWithSort", getSelectWithSort(fieldForSort))
        model.addAttribute("searchDocument", searchDocument)

        addCommonModels(httpRequest, httpResponse, model)

        return "pages/books-autograph";
    }

    private fun getSelectWithSort(fieldForSort: String?): Select {
        val select = Select()
        select.options.add(Option(value = "author", text = "по библиографическому описанию", selected = false))
        select.options.add(Option(value = "inv_number", text = "по инвентарному номеру", selected = false))
        select.setSelectedField(fieldForSort)
        return select
    }

    @GetMapping("/request-exhibition")
    fun requestExhibition(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String =
        createTemplateModel(REQUEST_EXHIBITION_MENU_ID, httpRequest, httpResponse, model, "pages/rgali-template-2")

    @GetMapping("/exhibition")
    fun exhibition(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String =
        createTemplateModel(EXHIBITION_MENU_ID, httpRequest, httpResponse, model, "pages/rgali-template-2")

    @GetMapping("/rewards")
    fun rewards(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String =
        createTemplateModel(REWARDS_MENU_ID, httpRequest, httpResponse, model,"pages/rgali-template-2")

    @GetMapping("/past")
    fun past(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String =
        createTemplateModel(PAST_DOCUMENTS_MENU_ID, httpRequest, httpResponse, model, "pages/rgali-template-2-menu")

    @GetMapping("/album-documents")
    fun albumDocuments(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String =
        createTemplateModel(ALBUMS_DOCUMENTS_MENU_ID, httpRequest, httpResponse, model, "pages/rgali-template-2-menu")

    @GetMapping("/rgali-staff-articles")
    fun staffArticles(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String =
        createTemplateModel(STAFF_ARTICLES_MENU_ID, httpRequest, httpResponse, model, "pages/rgali-template-1")

    @GetMapping("/rgali-scientific-conferences-participation")
    fun scientificConferencesParticipation(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String =
         createTemplateModel(SCIENTIFIC_CONFERENCES_PARTICIPATION_MENU_ID, httpRequest, httpResponse, model, "pages/rgali-template-1")

    @GetMapping("/unique-documents")
    fun uniqueDocuments(@RequestParam searchDocument: String?, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String =
        createTemplate3Model(UNIQUE_DOCUMENTS_MENU_ID, httpRequest, httpResponse, model,  searchDocument, false)

    @GetMapping("/memorial")
    fun memorial(@RequestParam searchDocument: String?, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String =
        createTemplate3Model(MEMORIAL_DOCUMENTS_MENU_ID, httpRequest, httpResponse, model,  searchDocument, false)

    @GetMapping("/tour")
    fun tour(
        httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        val pages = staticPagesService.getArticles(TOURS_MENU_ID)
        val menuItemInfo = staticPagesService.getMenuItemInfo(TOURS_MENU_ID)

        model.addAttribute("iconPath", menuItemInfo?.iconPath)
        model.addAttribute("title", menuItemInfo?.title)
        model.addAttribute("pages", pages)
        model.addAttribute("tourForm", TourForm())

        return "pages/rgali-template-4"
    }

    @PostMapping("/tour")
    fun sendTourRequest(
        @ModelAttribute tourForm: TourForm, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        val pages = staticPagesService.getArticles(TOURS_MENU_ID)
        val menuItemInfo = staticPagesService.getMenuItemInfo(TOURS_MENU_ID)
        emailService.sendTourForm(tourForm)

        model.addAttribute("iconPath", menuItemInfo?.iconPath)
        model.addAttribute("title", menuItemInfo?.title)
        model.addAttribute("pages", pages)
        model.addAttribute("tourForm", tourForm)

        if (httpRequest.requestURL.contains("rgali-anti-corruption")) {
            return "pages/rgali-anti-corruption"
        }
        return "pages/rgali-template-4"
    }

    @PostMapping("/cor")
    fun cor(
        @ModelAttribute tourForm: TourForm, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model): String {
        addCommonModels(httpRequest, httpResponse, model)

        return "pages/rgali-anti-corruption"
    }

    private fun createTemplate3Model(menuId: Long, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model,  searchDocument: String?, hideFirstImageNote: Boolean): String {
        model.addAttribute("hideFirstImageNote", hideFirstImageNote)
        addCommonModels(httpRequest, httpResponse, model)

        val totalPages  = staticPagesService.getPages(menuId)!!.map { it!!.articles }.flatMap { it!! }
            .filter { if (searchDocument == null || searchDocument.isBlank()) true
            else it?.text?.contains(searchDocument, true) == true }

        val pages = TemplateModel(pages = totalPages, page = Page())
        val menuItemInfo = staticPagesService.getMenuItemInfo(menuId)

        model.addAttribute("iconPath", menuItemInfo?.iconPath)
        model.addAttribute("title", menuItemInfo?.title)
        model.addAttribute("model", pages)
        model.addAttribute("searchDocument", searchDocument)

        return "pages/rgali-template-3"
    }

    private fun createTemplateModel(menuId: Long, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model, template: String): String {
        addCommonModels(httpRequest, httpResponse, model)

        val pages = staticPagesService.getPages(menuId)
        val years = staticPagesService.getYears(menuId)
        val menuItemInfo = staticPagesService.getMenuItemInfo(menuId)

        model.addAttribute("iconPath", menuItemInfo?.iconPath)
        model.addAttribute("title", menuItemInfo?.title)
        model.addAttribute("years", getYearsSelect("all", years))
        model.addAttribute("articleByYears", pages)

        return template
    }

    fun getSelectPage(selectedPage: String): Select {
        val select = Select()
        select.options.add(Option(value = "20", text = "20", selected = false))
        select.options.add(Option(value = "50", text = "50", selected = false))
        select.options.add(Option(value = "100", text = "100", selected = false))
        select.setSelectedField(selectedPage)
        return select
    }

    companion object {
        const val STAFF_ARTICLES_MENU_ID = 22L
        const val SCIENTIFIC_CONFERENCES_PARTICIPATION_MENU_ID = 23L
        const val UNIQUE_DOCUMENTS_MENU_ID = 30L
        const val MEMORIAL_DOCUMENTS_MENU_ID = 31L
        const val ALBUMS_DOCUMENTS_MENU_ID = 35L
        const val PAST_DOCUMENTS_MENU_ID = 36L
        const val REWARDS_MENU_ID = 12L
        const val EXHIBITION_MENU_ID = 25L
        const val REQUEST_EXHIBITION_MENU_ID = 26L
        const val TOURS_MENU_ID = 20L

        const val MEROP_MENU_ID = 5L
        const val DOSUG_MENU_ID = 6L
        const val WORK_DAYS_MENU_ID = 7L
        const val GUST_FRIENDS_ARCHIVE_MENU_ID = 8L
    }
}

data class ArticleByYears(
    var year: String?,
    var articles: List<Article?>?,
)

data class Article(
    var id: Long,
    var text: String?,
    var images: List<ImageSite?>? = emptyList(),
    var files: List<PageFile?>? = emptyList(),
)

private fun getYearsSelect(fieldForSort: String?, years: List<String?>?): Select {
    val select = Select()
    select.options.add(Option(value = "all", text = "    ", selected = true))

    years?.forEach{
        select.options.add(Option(value = it, text = it, selected = false))
    }
    select.setSelectedField(fieldForSort)

    return select
}

data class TourForm(
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var theme: String? = null,
    var message: String?= null,
)

