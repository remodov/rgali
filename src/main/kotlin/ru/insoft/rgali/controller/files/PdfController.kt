package ru.insoft.rgali.controller.files

import org.springframework.core.io.InputStreamResource
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import ru.insoft.rgali.config.ApplicationProperties
import ru.insoft.rgali.dao.FilesDAO
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.servlet.http.HttpServletResponse

@Controller
class PdfController(val applicationProperties: ApplicationProperties, val fileDAO: FilesDAO) {

    @RequestMapping(value = ["/pdf/{id}"], method = [RequestMethod.GET])
    fun getFielAsResponseEntity(@PathVariable id: Long, response: HttpServletResponse): ResponseEntity<InputStreamResource> {
        val file = fileDAO.findOne(id)
        val headers = HttpHeaders()

        file?.fileName?.let {
            val imagePath = fullPAth(it)
            val media: ByteArray = Files.readAllBytes(imagePath)
            headers.cacheControl = CacheControl.noCache().headerValue

            val split = it.split("/")
            response.setHeader("Content-Disposition", "attachment; filename=" + split[split.size - 1])

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))

                .body(InputStreamResource(ByteArrayInputStream(media)));
        }

        return ResponseEntity.notFound().build()
    }

    private fun fullPAth(link: String): Path =
        Paths.get(applicationProperties.imagePath, link)
}

