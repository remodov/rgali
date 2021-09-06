package ru.insoft.rgali.service

import org.springframework.stereotype.Service
import ru.insoft.rgali.controller.SearchForm
import ru.insoft.rgali.dao.*
import ru.insoft.rgali.entity.Page
import ru.insoft.rgali.entity.Sort
import ru.insoft.rgali.model.storageunit.StorageUnitListModel
import ru.insoft.rgali.model.storageunit.StorageUnitModel

@Service
class StorageUnitService(
    private val strageUnitDAO: StorageUnitDAO,
    private val opisDAO: OpisDAO,
    private val fundDAO: FundDAO,
    private val personDAO: PersonDAO,
    private val systemPartitionDAO: SystemPartitionDAO,
    private val organizationDAO: OrganizationDAO
) {
    fun getStorageListModel(systemId: Long?, opisId: Long?, fondId: Long, sort: Sort, page: Page): StorageUnitListModel {
        val storageUnits =
            when {
                systemId != null -> strageUnitDAO.getStorageUnitsBySystemId(systemId, sort, page)
                opisId != null -> strageUnitDAO.getStorageUnitsByOpisId(opisId, sort, page)
                else -> strageUnitDAO.getStorageUnitsByFondId(fondId, sort, page)
            }

        val countStorageUnits = strageUnitDAO.countStorageUnits(fondId, opisId, systemId);

        val opisCount = opisDAO.getOpisesByFondId(fondId).get().size

        return StorageUnitListModel(
            storageUnits = storageUnits,
            fund = fundDAO.getFond(fondId).get(),
            opisCount = opisCount,
            opis = if (opisId != null) opisDAO.getOpis(opisId).get() else null,
            systemPartition = if (systemId != null) systemPartitionDAO.getOpisSystemPartition(systemId) else null,
            page = page.copy(totalRows = countStorageUnits),
            sort = sort
        )
    }

    fun getStorageSearchModel(searchForm: SearchForm, sort: Sort, page: Page): StorageUnitListModel {
        val countStorageUnits = strageUnitDAO.countStorageUnits(searchForm)

        return StorageUnitListModel(
            storageUnits = strageUnitDAO.getStorageUnitsBySearch(searchForm, sort, page),
            page = page.copy(totalRows = countStorageUnits),
            sort = sort
        )
    }

    fun getStorageUnitModel(storageUnitId: Long): StorageUnitModel? {
        val storageUnit = strageUnitDAO.getStorageUnitById(storageUnitId)!!
        val fond = fundDAO.getFond(storageUnit.fondId!!).get()

        storageUnit.authorPersons = personDAO.getDealAuthorPerson(storageUnitId)
        storageUnit.persons = personDAO.getDealPerson(storageUnitId)
        storageUnit.organizations = organizationDAO.getOrganizationsByDealId(storageUnitId)
        storageUnit.authorOrganizations = organizationDAO.getOrganizationsAuthorByDealId(storageUnitId)
        storageUnit.playMethod = strageUnitDAO.loadPlayMethod(storageUnitId)
        storageUnit.materialType = strageUnitDAO.loadMaterialType(storageUnitId)
        storageUnit.theme = strageUnitDAO.loadTheme(storageUnitId)
        storageUnit.documentGroup = strageUnitDAO.loadDocumentGroup(storageUnitId)
        storageUnit.rubrika = strageUnitDAO.loadRubrika(storageUnitId)
        storageUnit.place = strageUnitDAO.loadPlace(storageUnitId)

        return StorageUnitModel(storageUnit, fond)
    }
}