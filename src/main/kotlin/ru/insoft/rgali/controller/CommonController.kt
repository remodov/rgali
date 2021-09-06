package ru.insoft.rgali.controller

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.Model
import org.springframework.web.util.WebUtils
import ru.insoft.rgali.config.RgaliUser
import ru.insoft.rgali.dao.AskSiteDAO
import ru.insoft.rgali.dao.NewsDAO
import ru.insoft.rgali.dao.PersonImagesDAO
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class CommonController(open val personImagesDAO: PersonImagesDAO,
                                open val askSiteDAO: AskSiteDAO,
                                open val newsDAO: NewsDAO,
) {

    fun addCommonModels(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model) {
        evaluateHeaderImages(httpRequest, httpResponse, model)
        addCountInRecycle(model)

        model.addAttribute("attention", newsDAO.getAttention()?.content)
        model.addAttribute("searchForm", SearchForm())
    }

    fun evaluateHeaderImages(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, model: Model) {
        val imageHeaderCookie =
            WebUtils.getCookie(httpRequest, IMAGE_HEADER_OFFSET_COOKIES_NAME)
                ?: Cookie(IMAGE_HEADER_OFFSET_COOKIES_NAME, IMAGE_HEADER_DEFAULT_OFFSET.toString())

        var imageHeaderOffset = imageHeaderCookie.value?.toInt() ?: IMAGE_HEADER_DEFAULT_OFFSET

        val totalPersonImages = personImagesDAO.countPersonImages()

        if (imageHeaderOffset >= totalPersonImages || imageHeaderOffset < 0) {
            imageHeaderOffset = 0
        } else {
            imageHeaderOffset += 6
        }

        model.addAttribute(IMAGE_HEADER_MODEL_NAME, personImagesDAO.getHeaderPersonImages(imageHeaderOffset))
        imageHeaderCookie.value = imageHeaderOffset.toString()

        httpResponse.addCookie(imageHeaderCookie)
    }

    fun addCountInRecycle(model: Model) {
        SecurityContextHolder.getContext().authentication?.let {
            if (it !is AnonymousAuthenticationToken) {
                val rgaliUser = it.principal as RgaliUser
                model.addAttribute("countInRecycle", askSiteDAO.countInRecycle(rgaliUser.id))
            }
        }
    }

    companion object {
        private const val IMAGE_HEADER_OFFSET_COOKIES_NAME = "image-header-offset"
        private const val IMAGE_HEADER_MODEL_NAME = "headerImages"
        private const val IMAGE_HEADER_DEFAULT_OFFSET = 0
    }

}