package ru.insoft.rgali.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import ru.insoft.rgali.dao.AskSiteDAO
import ru.insoft.rgali.dao.NewsDAO
import ru.insoft.rgali.dao.PersonImagesDAO
import ru.insoft.rgali.model.html.Option
import ru.insoft.rgali.model.html.Select
import ru.insoft.rgali.service.NewsService
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class NewsController(
    val newsService: NewsService,
    override val newsDAO: NewsDAO,
    override val askSiteDAO: AskSiteDAO,
    override val personImagesDAO: PersonImagesDAO
) : CommonController(personImagesDAO, askSiteDAO, newsDAO) {

    @GetMapping("/news-new/{id}")
    fun indexNew(httpRequest: HttpServletRequest,
                 httpResponse: HttpServletResponse,
                 model: Model,
                 @PathVariable id: Long): String {
        model.addAttribute("news", newsService.news(id))

        addCommonModels(httpRequest, httpResponse, model)

        return "pages/news/news"
    }

    @GetMapping("/news-arch")
    fun archNews(httpRequest: HttpServletRequest,
                 httpResponse: HttpServletResponse,
                 model: Model,
                 @RequestParam year: Int?,
                 @RequestParam month: Int?,
                 @RequestParam type: String?
    ): String
    {
        val searchYear = year ?: LocalDate.now().year
        val searchMonth = month ?: LocalDate.now().month.value

        model.addAttribute("years", getYears(searchYear.toString()))
        model.addAttribute("months", getSelectMonth(searchMonth.toString()))
        model.addAttribute("types", getSelectType(type.toString()))

        model.addAttribute("news", newsService.allArchNews(searchYear, searchMonth, if (type == "null") null else type?.toInt()))

        addCommonModels(httpRequest, httpResponse, model)

        return "pages/news/news-arch"
    }

    fun getYears(year: String): Select {
        val select = Select()
        (2006..LocalDate.now().year).forEach {
            select.options.add(Option(value = it.toString(), text = it.toString(), selected = false))
        }
        select.setSelectedField(year)
        return select
    }

    fun getSelectMonth(month: String): Select {
        val select = Select()
        select.options.add(Option(value = "1", text = "Январь", selected = false))
        select.options.add(Option(value = "2", text = "Февраль", selected = false))
        select.options.add(Option(value = "3", text = "Март", selected = false))
        select.options.add(Option(value = "4", text = "Апрель", selected = false))
        select.options.add(Option(value = "5", text = "Май", selected = false))
        select.options.add(Option(value = "6", text = "Июнь", selected = false))
        select.options.add(Option(value = "7", text = "Июль", selected = false))
        select.options.add(Option(value = "8", text = "Август", selected = false))
        select.options.add(Option(value = "9", text = "Сентябрь", selected = false))
        select.options.add(Option(value = "10", text = "Октябрь", selected = false))
        select.options.add(Option(value = "11", text = "Ноябрь", selected = false))
        select.options.add(Option(value = "12", text = "Декабрь", selected = false))
        select.setSelectedField(month)
        return select
    }

    fun getSelectType(type: String): Select {
        val select = Select()
        newsDAO.findAllNewsType().forEach {
            select.options.add(Option(value = it.id, text = it.text, selected = false))
        }
        select.options.add(Option(value = "null", text = "", selected = false))

        select.setSelectedField(type)
        return select
    }

}
