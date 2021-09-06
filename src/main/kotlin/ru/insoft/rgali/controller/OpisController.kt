package ru.insoft.rgali.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.insoft.rgali.dao.AskSiteDAO
import ru.insoft.rgali.dao.NewsDAO
import ru.insoft.rgali.dao.PersonImagesDAO
import ru.insoft.rgali.service.OpisService
import ru.insoft.rgali.service.SystematizationService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/opis")
class OpisController(
    private val opisService: OpisService,
    private val systematizationService: SystematizationService,
    override val newsDAO: NewsDAO,
    override val askSiteDAO: AskSiteDAO,
    override val personImagesDAO: PersonImagesDAO
) : CommonController(personImagesDAO, askSiteDAO, newsDAO) {

    @GetMapping("/{id}")
    fun opisCard(@PathVariable id: Long,
                 httpRequest: HttpServletRequest,
                 httpResponse: HttpServletResponse,
                 model: Model): String {
        model.addAttribute("model", opisService.getOpisCardModel(id))

        addCommonModels(httpRequest, httpResponse, model)

        return "pages/opis/opis-card"
    }

    @GetMapping
    fun opisList(
        @RequestParam fundId: Long?,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        model: Model,
    ): String {
        model.addAttribute("model", opisService.getOpisListModel(fundId))

        addCommonModels(httpRequest, httpResponse, model)

        return "pages/opis/opis-list"
    }

    @GetMapping("/{id}/systematization")
    fun opisSystematization(@PathVariable id: Long,
                            httpRequest: HttpServletRequest,
                            httpResponse: HttpServletResponse,
                            model: Model,): String {
        model.addAttribute("model", systematizationService.getSystematizationModel(id))

        addCommonModels(httpRequest, httpResponse, model)

        return "pages/systematization/systematization-section"
    }
}