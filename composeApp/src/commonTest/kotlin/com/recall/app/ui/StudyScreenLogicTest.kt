package com.recall.app.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class StudyScreenLogicTest {

    @Test
    fun initialStudyCardIndex_returnsZero_whenDeckIsEmpty() {
        assertEquals(0, initialStudyCardIndex(startIndex = 4, totalCards = 0))
    }

    @Test
    fun initialStudyCardIndex_clampsToDeckBounds() {
        assertEquals(0, initialStudyCardIndex(startIndex = -3, totalCards = 5))
        assertEquals(4, initialStudyCardIndex(startIndex = 9, totalCards = 5))
        assertEquals(2, initialStudyCardIndex(startIndex = 2, totalCards = 5))
    }

    @Test
    fun nextStudyCardIndex_wrapsAroundAtDeckEnd() {
        assertEquals(0, nextStudyCardIndex(currentCardIndex = 2, totalCards = 3))
        assertEquals(2, nextStudyCardIndex(currentCardIndex = 1, totalCards = 3))
    }

    @Test
    fun nextStudyCardIndex_returnsZero_whenDeckIsEmpty() {
        assertEquals(0, nextStudyCardIndex(currentCardIndex = 10, totalCards = 0))
    }
}
