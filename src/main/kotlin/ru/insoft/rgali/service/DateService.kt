package ru.insoft.rgali.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.dao.*
import ru.insoft.rgali.entity.AskEntity
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.ResearchThemeEntity
import ru.insoft.rgali.entity.Sort
import ru.insoft.rgali.model.ask.AskListModel
import ru.insoft.rgali.model.fund.FundCardModel
import ru.insoft.rgali.model.fund.FundListModel
import java.time.LocalDate

@Service
class DateService(
    private val holidayDAO: HolidayDAO
) {

    fun getAskDate(askDate: LocalDate, defaultShiftDate: Long = 4): LocalDate {
        var shiftDays = defaultShiftDate

        var noHolidays = shiftDays + 1
        var i = 0L
        while(noHolidays != 0L) {
            if (holidayDAO.isHoliday(askDate.plusDays(i))) {
                shiftDays++
                noHolidays++
            }
            noHolidays--
            i++
        }

        return askDate.plusDays(shiftDays)
    }
}