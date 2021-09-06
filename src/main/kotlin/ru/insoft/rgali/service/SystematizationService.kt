package ru.insoft.rgali.service

import org.springframework.stereotype.Service
import ru.insoft.rgali.dao.FundDAO
import ru.insoft.rgali.dao.OpisDAO
import ru.insoft.rgali.dao.SystematizationDAO
import ru.insoft.rgali.entity.SystematizationEntity
import ru.insoft.rgali.model.systematization.SystematizationModel


@Service
class SystematizationService(
    private val systematizationDAO: SystematizationDAO,
    private val opisDAO: OpisDAO,
    private val fundDAO: FundDAO
) {
    fun getSystematizationModel(opisId: Long): SystematizationModel {
        val systematizationModel = SystematizationModel()
        systematizationModel.opis = opisDAO.getOpis(opisId).get()
        systematizationModel.fund = fundDAO.getFond(systematizationModel.opis!!.fondId!!).get()

        val systematizationHierarchy = systematizationDAO.getSystematizationByOpisId(opisId)

        val systematizationByLevel = systematizationHierarchy?.groupBy { i -> i.level }

        val maxLevel = systematizationByLevel?.size!!

        val rootElements = systematizationByLevel.get(1)

        rootElements?.forEach { rootElement ->

            val elementsTree = mutableListOf(rootElement)
            val elementsTreeIds = mutableListOf(rootElement.id)

            for (level in (2..maxLevel)) {

                systematizationByLevel[level]?.forEach {
                    if (elementsTreeIds.contains(it.parentId)) {
                        elementsTree.add(it)
                        elementsTreeIds.add(it.id)
                    }
                }
            }

            var currentElement = elementsTree.get(0)
            var sortedList = mutableListOf<SystematizationEntity>()
            sort(currentElement, elementsTree, sortedList)

            systematizationModel.systematizationHierarchy.addAll(sortedList)
        }

        return systematizationModel
    }

    private fun sort(
        systematizationEntity: SystematizationEntity,
        all: MutableList<SystematizationEntity>,
        sorted: MutableList<SystematizationEntity>
    ) {
        val id = systematizationEntity.id
        sorted.add(systematizationEntity)

        val children = all.filter { it.parentId == id }

        if (children.isEmpty()) {
            systematizationEntity.isLeaf = true
        }

        children.forEach { child ->
            sort(child, all, sorted)
        }
    }
}