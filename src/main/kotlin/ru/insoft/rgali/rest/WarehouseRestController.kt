package ru.insoft.rgali.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.insoft.rgali.dao.FundDAO
import ru.insoft.rgali.dao.LiterDAO
import ru.insoft.rgali.dao.PersonDAO
import ru.insoft.rgali.dao.StorageUnitDAO
import ru.insoft.rgali.dto.SelectDto
import ru.insoft.rgali.entity.PersonShortEntity
import ru.insoft.rgali.entity.StorageUnitEntity

@RestController
class WarehouseRestController(
    private val fundDAO: FundDAO,
    private val storageUnitDAO: StorageUnitDAO
) {
    @GetMapping("/api/v1/warehouse")
    fun getWarehouseByFondNumber(
        @RequestParam fondNumber: Int,
        @RequestParam opisNumber: Int,
        @RequestParam dealNumber: Int,
        @RequestParam suffixOpis: String?,
        @RequestParam suffixDeal: String?,
    ): ResponseEntity<String?> {

        storageUnitDAO.findDealByKey(fondNumber.toLong(), opisNumber.toLong(), dealNumber.toLong(), suffixOpis, suffixDeal)?.let {
            if (storageUnitDAO.isDealCopy(it.id!!) == true) {
                return ResponseEntity.ok(fundDAO.getWareHouseName(3))
            }
        }

        val wareHouseId = fundDAO.getWareHouseId(fondNumber)
        return ResponseEntity.ok(fundDAO.getWareHouseName(wareHouseId))
    }
}