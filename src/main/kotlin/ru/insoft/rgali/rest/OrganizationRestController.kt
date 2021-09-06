package ru.insoft.rgali.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.insoft.rgali.dao.OrganizationDAO
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.OrganizationEntity

@RestController
class OrganizationRestController(private val organizationDAO: OrganizationDAO){

    @GetMapping("/api/v1/organization/{name}")
    fun getAllOrganizationsByName(@PathVariable name: String): List<SelectDto> {
        return organizationDAO.findAllByName(name.split("?")[0]).map { SelectDto(id = it.orgName, text = it.orgName) }
    }
}