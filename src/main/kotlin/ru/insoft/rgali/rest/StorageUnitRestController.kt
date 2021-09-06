package ru.insoft.rgali.rest

import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.config.RgaliUser
import ru.insoft.rgali.dao.AskDAO
import ru.insoft.rgali.dao.LiterDAO
import ru.insoft.rgali.dao.PersonDAO
import ru.insoft.rgali.dao.StorageUnitDAO
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.PersonShortEntity
import ru.insoft.rgali.entity.StorageUnitEntity
import ru.insoft.rgali.service.AskService

@RestController
class StorageUnitRestController(
    private val storageUnitDAO: StorageUnitDAO,
    private val askService: AskService,
    private val askDAO: AskDAO,
    ) {

    @GetMapping("/api/v1/storage-unit")
    fun getAllPersons(
        @RequestParam fondNumber: Long,
        @RequestParam opisNumber: Long,
        @RequestParam dealNumber: Long,
        @RequestParam suffixOpis: String?,
        @RequestParam suffixDeal: String?,
        authentication: Authentication
    ): ResponseEntity<StorageUnitEntity> {
        val rgaliUser = authentication.principal as RgaliUser

        val findDealByKey = storageUnitDAO.findDealByKey(fondNumber, opisNumber, dealNumber, suffixOpis, suffixDeal)
            ?: return ResponseEntity.ok(StorageUnitEntity(displayNumber = "Единица хранения не найдена в базе данных. Исправьте реквизиты или заполните поле «Заголовок единицы хранения»"))

        if (askService.isDealExists(rgaliUser.id, findDealByKey.id!!.toLong())) {
            return ResponseEntity.ok(StorageUnitEntity(title = "Выбранная единица хранения уже есть в вашей корзине!"))
        }

        if (askDAO.isDealRequested(findDealByKey.id!!.toLong())) {
            return ResponseEntity.ok(StorageUnitEntity(title = "Выбранная единица хранения уже заказана!"))
        }

        return ResponseEntity.ok(findDealByKey)
    }
}