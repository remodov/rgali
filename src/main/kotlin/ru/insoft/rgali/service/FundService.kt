package ru.insoft.rgali.service

import org.springframework.stereotype.Service
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.dao.DocumentGroupDAO
import ru.insoft.rgali.dao.FundDAO
import ru.insoft.rgali.dao.OrganizationDAO
import ru.insoft.rgali.dao.PersonDAO
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort
import ru.insoft.rgali.model.fund.FundCardModel
import ru.insoft.rgali.model.fund.FundListModel

@Service
class FundService(
    private val fundDAO: FundDAO,
    private val personDAO: PersonDAO,
    private val organizationDAO: OrganizationDAO,
    private val documentGroupDAO: DocumentGroupDAO
) {
    fun getFundsModel(fundType: String?, sort: Sort?, page: Page): FundListModel {
        val fonds = fundDAO.getFonds(fundType, sort!!, page)
        val fundListModel = FundListModel()
        if (fonds.isPresent) {
            page.totalRows = fundDAO.countFonds(fundType)
            fundListModel.funds = fonds.get()
            fundListModel.page = page
            fundListModel.sort = sort
        }
        return fundListModel
    }

    fun getFundsModel(fundType: String?, sort: Sort?, page: Page, searchForm: SearchForm): FundListModel {
        val fonds = fundDAO.getFonds(fundType, sort!!, page, searchForm)
        val fundListModel = FundListModel()
        if (fonds.isPresent) {
            page.totalRows = fundDAO.countFonds(fundType, searchForm)
            fundListModel.funds = fonds.get()
            fundListModel.page = page
            fundListModel.sort = sort
        }
        return fundListModel
    }

    fun getFundModel(id: Long): FundCardModel {
        val fond = fundDAO.getFond(id)
        var fundCardModel = FundCardModel()
        if (fond.isPresent) {
            val fundFullEntity = fond.get()
            personDAO.getPersonsByFundId(id).ifPresent {
                fundFullEntity.fondPersons = it
            }
            fundFullEntity.organizations =
                organizationDAO.getOrganizationsByFondId(id)
            fundFullEntity.documentGroup = documentGroupDAO.findByFondId(id)

            fundCardModel = FundCardModel(fundFullEntity)
        }
        return fundCardModel
    }
}