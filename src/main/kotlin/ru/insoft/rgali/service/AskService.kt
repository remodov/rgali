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

@Service
class AskService(
    private val askDAO: AskDAO,
    private val askSiteDAO: AskSiteDAO,
    private val researchThemeDAO: ResearchThemeDAO,
    private val storageUnitDAO: StorageUnitDAO,
    private val userDAO: UserDAO,
    private val purposeResearchDAO: PurposeResearchDAO,
    private val fundDAO: FundDAO

) {
    fun getAsksModel(researcherId: Long, sort: Sort, page: Page): AskListModel {
        val asks = askDAO.findAll(researcherId, sort, page)
        val askListModel = AskListModel()

        page.totalRows = askDAO.count(researcherId)

        askListModel.asks = asks
        askListModel.page = page
        askListModel.sort = sort

        return askListModel
    }

    fun getAsksSiteModel(researcherId: Long): List<AskEntity> =
        askSiteDAO.findAll(researcherId)

    fun getAsksSite(askId: Long): AskEntity =
        askSiteDAO.findOne(askId)!!

    @Transactional
    fun createAsk(askEntity: AskEntity) {
        askDAO.save(askEntity)
    }

    @Transactional
    fun createAskSite(dealId: Long, userEmail: String) : AskEntity {
        val storageUnit = storageUnitDAO.find(dealId)
        val user = userDAO.findByEmail(userEmail)

        val theme = if (user?.workTheme != null && user.workTheme!!.contains(Regex("\\d+"))) {
            researchThemeDAO.find(user.workTheme!!.toLong())
        } else
            researchThemeDAO.findAll(user?.workTheme).firstOrNull()

        val goal = if (user?.goal != null && user.goal!!.contains(Regex("\\d+"))) {
            purposeResearchDAO.find(user.goal!!.toLong())
        } else
            purposeResearchDAO.findAll(user?.goal).first()

        val wareHouseId =
            if (storageUnitDAO.isDealCopy(storageUnit?.id!!) == true) 3 else fundDAO.getWareHouseId(storageUnit.num_f?.toInt())

        val askEntity = AskEntity(
            cypher = storageUnit.displayNumber,
            headerDoc = storageUnit.title,
            numLists = storageUnit.pages,
            num_f = storageUnit.num_f,
            num_o = storageUnit.num_o,
            num_d = storageUnit.num_d,
            dealId = dealId,
            literalOpis = storageUnit.suffix_o,
            literalDeal = storageUnit.suffix,

            warehouseName = "null",
            purposeResearchName = goal?.text,
            purposeResearchTypeId = goal?.id?.toLong(),
            themename = theme?.text,
            researchThemeId = theme?.id?.toLong(),
            askStateTypeId = 9,
            warehouseTypeId = wareHouseId,
            researcherId = user?.id,
        )
        askEntity.askNum = askDAO.getId()

        askSiteDAO.save(askEntity)

        return askEntity
    }

    fun isDealExists(researcherId: Long, dealId: Long) : Boolean = askSiteDAO.isDealExists(researcherId, dealId) ?: false

    fun delete(askSiteId: Long)  = askSiteDAO.delete(askSiteId)

}