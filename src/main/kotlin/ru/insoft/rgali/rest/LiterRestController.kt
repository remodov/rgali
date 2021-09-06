package ru.insoft.rgali.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.insoft.rgali.dao.LiterDAO
import ru.insoft.rgali.dao.PersonDAO
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.PersonShortEntity

@RestController
class LiterRestController(private val literDAO: LiterDAO) {

    @GetMapping("/api/v1/liter")
    fun getAllPersons(@RequestParam("_type") type: String, @RequestParam("term") term: String?): List<SelectDto> {
        return literDAO.findAll()
    }
}