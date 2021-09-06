package ru.insoft.rgali.rest

import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.config.RgaliUser
import ru.insoft.rgali.controller.dateFormat
import ru.insoft.rgali.dao.*
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.PersonShortEntity
import ru.insoft.rgali.entity.StorageUnitEntity
import java.time.LocalDate
import java.time.temporal.TemporalAccessor

@RestController
class HolidayRestController(
    private val holidayDAO: HolidayDAO,
    private val askDAO: AskDAO) {

    @Secured("USER")
    @GetMapping("/api/v1/check-ask-date")
    fun getHoliday(@RequestParam date: String, authentication: Authentication): ResponseEntity<SelectDto> {
        val rgaliUser = authentication.principal as RgaliUser

        val askDate = if (date.contains(".")) LocalDate.parse(date, dateFormat) else LocalDate.parse(date)
        val now = LocalDate.now()

        if (!(askDate >= now.plusDays(4))) {
            return ResponseEntity.ok(
                SelectDto(
                    id = "false",
                    text = "Выбранная дата должны быть больше на 4 дня от текущей"
                )
            )
        }

        val holiday = holidayDAO.isHoliday(askDate)

        if (holiday) {
            return ResponseEntity.ok(
                SelectDto(
                    id = "false",
                    text = "Читальный зал на выбранную дату не работает, выберите другую дату"
                )
            )
        }

        if (askDAO.countOpenedTasks(rgaliUserId = rgaliUser.id, askDate) > 20) {
            return ResponseEntity.ok(
                SelectDto(
                    id = "false",
                    text = "На выбранную дату вы можете заказать не более 20 единиц хранения"
                )
            )
        }

        return ResponseEntity.ok(SelectDto(id = "true", text = null))
    }
}