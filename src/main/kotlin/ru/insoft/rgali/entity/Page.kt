package ru.insoft.rgali.entity

import java.math.BigDecimal
import java.math.RoundingMode

data class Page(
    var totalRows: Long = 0L,
    var rowsPerPage: Long = 20L,
    var currentPage: Long = 1L,
) {
    val offset: Long
        get() = rowsPerPage * (currentPage - 1)

    val currentElement: Long
        get() = rowsPerPage * (currentPage)

    val totalPages: Long
        get() {
            val lastPage = if (totalRows % rowsPerPage != 0L) 1 else 0
            return BigDecimal.valueOf(totalRows).divide(BigDecimal.valueOf(rowsPerPage), 0, RoundingMode.FLOOR)
                .add(BigDecimal.valueOf(lastPage.toLong())).toBigInteger().toLong()
        }

    fun nextPage(): Long {
        return if (currentPage >= totalPages) totalPages else currentPage + 1
    }

    fun previousPage(): Long {
        return if (currentPage <= 1) 1 else currentPage - 1
    }

    fun firstVisiblePage(): Long =
        if ((currentPage - MAX_PAGES) <= 0) 1 else currentPage - MAX_PAGES

    fun lastVisiblePage(): Long =
        if ((currentPage + MAX_PAGES) >= totalPages) totalPages else currentPage + MAX_PAGES

    companion object {
        const val MAX_PAGES = 5
    }
}