package ru.insoft.rgali.entity

data class StorageUnitEntity(
    var id: Long? = null,
    var number: String? = null,
    var displayNumber: String? = null,
    var title: String? = null,
    var annotation: String? = null,
    var playMethod: String? = null,
    var dates: String? = null,
    var pages: String? = null,
    var systemPartitionName: String? = null,
    var systemPartitionId: Long? = null,
    var fondId: Long? = null,
    var opisId: Long? = null,

    var person: String? = null,
    var personAuthor: String? = null,
    var systemPartition: String? = null,
    var materialType: String? = null,
    var num_d: String? = null,
    var num_o: String? = null,
    var num_f: String? = null,
    var suffix: String? = null,
    var suffix_o: String? = null,
    var barcode: String? = null,
    var importantCategoryId: String? = null,

    var authorPersons: List<PersonEntity> = listOf(),
    var persons: List<PersonEntity> = listOf(),
    var authorOrganizations: List<OrganizationEntity> = listOf(),
    var organizations: List<OrganizationEntity> = listOf(),
    var theme: List<IdValueEntity> = listOf(),
    var documentGroup: List<IdValueEntity> = listOf(),
    var rubrika: List<IdValueEntity> = listOf(),
    var place: List<IdValueEntity> = listOf(),

)