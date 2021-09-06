package ru.insoft.rgali.utils

import ru.insoft.rgali.controller.SearchForm

object SearchUtils {

    fun createWhereSectionPersons(searchForm: SearchForm): String {
        return " and upper(fio) like upper('%${searchForm.personaName}%') "
    }

    fun createWhereSectionOrganizations(searchForm: SearchForm): String? {
        return " and upper(org_name) like upper('%${searchForm.organizationName}%') "
    }

    fun createWhereSectionDeals(searchForm: SearchForm): String? {
        val whereSection = StringBuilder()

        if ((!searchForm.simpleSearch) && searchForm.fundNumber?.isNotBlank() == true) {
            whereSection.append(" and numf = ${searchForm.fundNumber} ")
        }

        if ((!searchForm.simpleSearch) && searchForm.opisNumber?.isNotBlank() == true) {
            whereSection.append(" and numop = '${searchForm.opisNumber}' ")
        }

        if ((!searchForm.simpleSearch) && searchForm.storageUnitNumber?.isNotBlank() == true) {
            whereSection.append(" and numd = ${searchForm.storageUnitNumber} ")
        }

        if (searchForm.fondName?.isNotBlank() == true) {
//            whereSection.append(" and upper(title) like upper('%${searchForm.fondName}%') ")
            whereSection.append(" and to_tsvector('russian', title::text) @@ plainto_tsquery('russian', '${searchForm.fondName}') ")
        }

        if ((!searchForm.simpleSearch) && searchForm.personaName?.isNotBlank() == true) {
//            whereSection.append(" and upper(fioperson) like upper('%${searchForm.personaName}%') ")
            if (searchForm.personType?.isNotBlank() == true) {
                if (searchForm.personType == "1" ) {
//            whereSection.append(" and upper(fioperson) like upper('%${searchForm.personaName}%') ")
                    if (searchForm.personId?.isNotBlank() == true) {
                        whereSection.append(" and to_tsvector('russian', idperson_pers::text) @@ plainto_tsquery('russian', '${searchForm.personId}') ")
                    } else {
                        whereSection.append(" and to_tsvector('russian', fioperson_pers::text) @@ plainto_tsquery('russian', '${searchForm.personaName}') ")
                    }
                }

                if (searchForm.personType == "2") {
                    if (searchForm.personId?.isNotBlank() == true) {
                        whereSection.append(" and to_tsvector('russian', idperson_auth::text) @@ plainto_tsquery('russian', '${searchForm.personId}') ")
                    } else {
                        whereSection.append(" and to_tsvector('russian', fioperson_auth::text) @@ plainto_tsquery('russian', '${searchForm.personaName}') ")
                    }
                }
            } else {
                whereSection.append(" and to_tsvector('russian', fioperson::text) @@ plainto_tsquery('russian', '${searchForm.personaName}') ")
            }
        }

        if ((!searchForm.simpleSearch) && searchForm.organizationName?.isNotBlank() == true) {
//            whereSection.append(" and upper(nameorg) like upper('%${searchForm.organizationName}%') ")
            whereSection.append(" and to_tsvector('russian', nameorg::text) @@ plainto_tsquery('russian', '${searchForm.organizationName}') ")
        }

        if ((!searchForm.simpleSearch) && searchForm.yearDocumentFrom?.isNotBlank() == true) {
            whereSection.append(" and CAST(NULLIF(start_date,'0') as INTEGER) >= ${searchForm.yearDocumentFrom} ")
        }

        if ((!searchForm.simpleSearch) && searchForm.yearDocumentTo?.isNotBlank() == true) {
            whereSection.append(" and CAST(NULLIF(end_date,'0') as INTEGER) <= ${searchForm.yearDocumentTo} ")
        }

        return whereSection.toString()
    }

    fun createWhereSectionFonds(searchForm: SearchForm): String? {
        val whereSection = StringBuilder()

        if ((!searchForm.simpleSearch) && searchForm.fundNumber?.isNotBlank() == true) {
            whereSection.append(" and \"number\" = ${searchForm.fundNumber} ")
        }

        if (searchForm.fondName?.isNotBlank() == true) {
            whereSection.append(" and upper(name) like upper('%${searchForm.fondName}%') ")
        }

        if ((!searchForm.simpleSearch) && searchForm.personaName?.isNotBlank() == true) {
            whereSection.append(" and upper(fioperson) like upper('%${searchForm.personaName}%') ")
        }

        if ((!searchForm.simpleSearch) && searchForm.organizationName?.isNotBlank() == true) {
            whereSection.append(" and upper(nameorg) like upper('%${searchForm.organizationName}%') ")
        }

        if ((!searchForm.simpleSearch) && searchForm.yearDocumentFrom?.isNotBlank() == true) {
            whereSection.append(" and CAST(NULLIF(edge_start,'0') as INTEGER) >= ${searchForm.yearDocumentFrom} ")
        }

        if ((!searchForm.simpleSearch) && searchForm.yearDocumentTo?.isNotBlank() == true) {
            whereSection.append(" and CAST(NULLIF(edge_end,'0') as INTEGER) <= ${searchForm.yearDocumentTo} ")
        }

        return whereSection.toString()
    }


}