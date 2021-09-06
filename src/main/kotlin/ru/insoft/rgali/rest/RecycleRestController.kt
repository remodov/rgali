package ru.insoft.rgali.rest

import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.config.RgaliUser
import ru.insoft.rgali.controller.RecycleForm
import ru.insoft.rgali.dao.*
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.service.AskService

@RestController
class RecycleRestController(
    private val askService: AskService,
    private val purposeResearchDAO: PurposeResearchDAO,
    private val storageUnitDAO: StorageUnitDAO,
    private val askDAO: AskDAO,
    private val askSiteDAO: AskSiteDAO
) {

    @Secured("USER")
    @GetMapping("/api/v1/recycle/{dealId}")
    fun create(@PathVariable dealId: Long, authentication: Authentication): ResponseEntity<String> {
        val rgaliUser = authentication.principal as RgaliUser

        if (askService.isDealExists(rgaliUser.id, dealId)) {
            return ResponseEntity.ok("Выбранная единица хранения уже есть в вашей корзине!")
        }

        if (askDAO.isDealRequested(dealId)) {
            return ResponseEntity.ok("Выбранная единица хранения уже заказана!")
        }

        askService.createAskSite(dealId, rgaliUser.email!!)

        return ResponseEntity.ok("Выбранная единица успешно добавлена в Вашу корзину!")
    }

    @Secured("USER")
    @GetMapping("/api/v1/recycle/correct/{dealId}")
    fun isDealIdCorrect(@PathVariable dealId: Long): ResponseEntity<String> {

        if (askDAO.isDealRequested(dealId)) {
            return ResponseEntity.ok("Выбранная единица хранения уже заказана!")
        }

        return ResponseEntity.ok().build()
    }

    @Secured("USER")
    @PostMapping("/api/v1/recycle/{askSiteId}")
    fun delete(@PathVariable askSiteId: Long, authentication: Authentication): ResponseEntity<String> {
        askService.delete(askSiteId)
        return ResponseEntity.ok().build()
    }

    @Secured("USER")
    @PostMapping("/api/v1/recycle/requirements")
    fun newFromAskSiteId(
        @RequestBody recycleForm: RecycleForm,
        authentication: Authentication
    ): ResponseEntity<String> {
        val rgaliUser = authentication.principal as RgaliUser

        recycleForm.askIds?.forEach { askId ->
            val asksSite = askService.getAsksSite(askId)

            val goal = recycleForm.goalId?.let { purposeResearchDAO.find(it) }
            val storageUnit = storageUnitDAO.find(asksSite.dealId!!)

            askService.createAsk(
                asksSite.copy(
                    purposeResearchName = goal?.text,
                    purposeResearchTypeId = goal?.id?.toLong(),
                    themename = recycleForm.themeId.toString(),
                    requestDate = recycleForm.requestDate,
                    askDate = recycleForm.askDate,
                    cypher = storageUnit?.displayNumber,
                    headerDoc = storageUnit?.title,
                    numLists = storageUnit?.pages,
                    num_f = storageUnit?.num_f,
                    num_o = storageUnit?.num_o,
                    num_d = storageUnit?.num_d,
                    literalOpis = storageUnit?.suffix_o,
                    literalDeal = storageUnit?.suffix,
                    askNum = askDAO.getId(),
                    researcherId = rgaliUser.id,
                )
            )

            askSiteDAO.delete(askId)
        }

        return ResponseEntity.ok().build()
    }
}