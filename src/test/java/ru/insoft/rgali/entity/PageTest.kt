package ru.insoft.rgali.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


open class PageTest {

    @Test
    fun testCountPagesWhenTotalRowsMoreThenRowsPerPage() {
        val page = Page(totalRows = 10L)

        assertEquals(1L, page.totalPages)
    }

    @Test
    fun testCountPagesWhenTotalRowsEqualsRowsPerPage() {
        val page = Page(totalRows = 20L)
        assertEquals(1L, page.totalPages)
    }


    @Test
    fun testCountPagesWhenTotalRowsGreatRowsPerPage() {
        val page = Page(totalRows = 21L)
        assertEquals(2L, page.totalPages)
    }

    @Test
    fun testCountPagesWhenTotalRowsMoreThenRowsPerPageAndTotalRowsRoundGreateHalf() {
        val page = Page(totalRows = 16L)
        assertEquals(1L, page.totalPages)
    }

}

open class A {
    init {
            kek()
    }

    open fun kek() {
        println("a")
    }
}