package ru.insoft.rgali.controller.files

import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import ru.insoft.rgali.config.ApplicationProperties
import ru.insoft.rgali.dao.ImageDAO
import ru.insoft.rgali.dao.ImageExtensionsDAO
import ru.insoft.rgali.dao.PersonDAO
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Controller
class ImageController(
    val applicationProperties: ApplicationProperties,
    val imageDAO: ImageDAO,
    val imageExtensionsDAO: ImageExtensionsDAO
) {

    @RequestMapping(value = ["/image/{id}/{size}"], method = [RequestMethod.GET])
    fun getImageAsResponseEntity(
        @PathVariable id: Long,
        @PathVariable(required = false) size: String = "n"
    ): ResponseEntity<InputStreamResource> {
        try {
            val image = imageDAO.findOne(id)
            val imageExtensions = imageExtensionsDAO.getImageExtensions()[size]
            val headers = HttpHeaders()

            image?.path?.let {
                val additionalFolder = Paths.get(image.path).fileName.toString().split(".")[0]
                val fullPathWithoutExtension = image.path.split(".")[0]

                var media: ByteArray? = null
                var mediaType = "jpeg"
                imageExtensions?.map { it.trim() }?.forEach { fileExtension ->
                    try {
                        val imageWithSizePath =
                            if (size == "n")
                                Paths.get(fullPathWithoutExtension, "$additionalFolder.$fileExtension")
                            else
                                Paths.get(fullPathWithoutExtension, "$additionalFolder$size.$fileExtension")

                        val imagePath = getImageLink(imageWithSizePath.toString())
                        media = Files.readAllBytes(imagePath)
                        mediaType = "$fileExtension"

                        return@forEach
                    } catch (e: Exception) {
                        logger.debug("Error when try to get image [$id - $size]: ${e.message}")
                    }
                }

                headers.cacheControl = CacheControl.noCache().headerValue

                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/$mediaType"))
                    .body(InputStreamResource(ByteArrayInputStream(media)));
            }
        } catch (e: Exception) {
            logger.error("Error when try to get image [$id - $size]: ${e.message}")
        }

        return ResponseEntity.notFound().build()
    }

    @RequestMapping(value = ["/image"], method = [RequestMethod.GET])
    fun getImage(@RequestParam path: String): ResponseEntity<InputStreamResource> {
        val headers = HttpHeaders()

        val media: ByteArray = Files.readAllBytes(getImageLink(path))
        headers.cacheControl = CacheControl.noCache().headerValue

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("image/jpeg"))
            .body(InputStreamResource(ByteArrayInputStream(media)));
    }

    private fun getImageLink(link: String): Path =
        Paths.get(applicationProperties.imagePath, link)


    companion object {
        private val logger = LoggerFactory.getLogger(ImageController::class.java)
    }
}

