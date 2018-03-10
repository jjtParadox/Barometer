package com.jjtparadox.barometer.otherpackage.test

import com.jjtparadox.barometer.tester.BarometerTester
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(BarometerTester::class)
class BarometerExampleTest {

    @Test
    fun testThatTestCountingWorksAcrossMultiplePackages() {
        println("Picked up the test in the other package!")
    }

}
