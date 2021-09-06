package ru.insoft.rgali.entity

data class FundFullEntity(
    var opises: Long? = null,
    var prints: Long? = null,
    var fondName: String? = null,
    var createYear: String? = null,
    var fondType: String? = null,
    var annotation: List<String> = ArrayList(),
    var fondPersons: List<PersonEntity> = ArrayList(),
    var organizations: List<OrganizationEntity> = ArrayList(),
    var documentGroup: List<DocumentGroupEntity> = ArrayList(),
    //fixme in
    var id: Long? = null,
    var fondNumber: Long? = null,
    var displayFondNumber: String? = null,
    var fullFondName: String? = null,
    var extremeDates: String? = null,
    var units: Long? = null,
    var warehouseTypeId: Long? = null
) {
    constructor(fundEntity: FundEntity) : this() {
        this.id = fundEntity.id
        this.fondNumber = fundEntity.fondNumber
        this.displayFondNumber = fundEntity.displayFondNumber
        this.fullFondName = fundEntity.fullFondName
        this.extremeDates = fundEntity.extremeDates
        this.units = fundEntity.units
    }
}



