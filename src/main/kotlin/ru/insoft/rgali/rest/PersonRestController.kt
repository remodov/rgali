package ru.insoft.rgali.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.insoft.rgali.dao.PersonDAO
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.PersonShortEntity

@RestController
class PersonRestController(private val personDao: PersonDAO) {

    @GetMapping("/api/v1/person/{name}")
    fun getAllPersons(@PathVariable name: String): List<SelectDto> {
        return personDao.findAll(name.split("?")[0]).map { SelectDto(id = it.fio, text = it.fio) }
    }

}