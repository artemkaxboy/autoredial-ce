package com.artemkaxboy.android.autoredialce.contacts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class MyPhoneTest {

    @Test
    fun compare() {
        val o0 = MyPhone()
        assertEquals(o0, o0)

        val o1 = MyPhone()
        assertEquals(o0, o1)
        o1.id = (Math.random() * Int.MAX_VALUE).roundToLong() + 1
        assertNotEquals(o0, o1)

        val o2 = MyPhone()
        o2.label = "label1"
        assertNotEquals(o0, o2)

        val o3 = MyPhone()
        o3.number = "9130102596"
        assertNotEquals(o0, o3)

        val o4 = MyPhone()
        o4.setType((Math.random() * Int.MAX_VALUE).roundToInt() + 1)
        assertNotEquals(o0, o4)

        o1.label = o2.label
        o1.number = o3.label
        o1.setType(o4.type)

        o2.id = o1.id
        o2.number = o1.number
        o2.setType(o1.type)
        assertEquals(o1, o2)
    }
}