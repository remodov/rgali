package ru.insoft.rgali.service

import org.springframework.stereotype.Service
import ru.insoft.rgali.dao.FundDAO
import ru.insoft.rgali.dao.OpisDAO
import ru.insoft.rgali.entity.FundFullEntity
import ru.insoft.rgali.model.opis.OpisCardModel
import ru.insoft.rgali.model.opis.OpisListModel

@Service
class OpisService(
    private val opisDAO: OpisDAO,
    private val fundDAO: FundDAO
) {
    fun getOpisListModel(fundId: Long?): OpisListModel {
        val opisListModel = OpisListModel()
        val opises = opisDAO.getOpisesByFondId(fundId!!)
        if (opises.isPresent) {
            opisListModel.opises = opises.get()
            val fond = fundDAO.getFond(fundId)
            fond.ifPresent { fund: FundFullEntity? -> opisListModel.fund = fund }
        }
        return opisListModel
    }

    fun getOpisCardModel(opisId: Long): OpisCardModel {
        val opisCardModel = OpisCardModel()
        val opis = opisDAO.getOpis(opisId)
        if (opis.isPresent) {
            opisCardModel.opis = opis.get()
            val fond = fundDAO.getFond(opisCardModel.opis!!.fondId!!)
            fond.ifPresent { fund: FundFullEntity? -> opisCardModel.fund = fund }
        }
        return opisCardModel
    }
}