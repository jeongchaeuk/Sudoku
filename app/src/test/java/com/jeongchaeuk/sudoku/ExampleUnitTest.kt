package com.jeongchaeuk.sudoku

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_Sudoku() {
        val game = Sudoku()
        game.makeSample()

        var loopCount = 0
        while (game.checkGroups()) {
            ++loopCount
        }
        println(game)
        println("checkGroup try count = $loopCount")

        game.checkColumns()
        println(game)
//
        game.checkRows()
        println(game)

    }

    @Test
    fun testList() {
        val list1 = listOf(Foo(0,1), Foo(1,2), Foo(2,3), Foo(3,1), Foo(4,1))
        println("list1=$list1")

//        println(list1.count { it.value == list1[0].value })

        val list2 = list1.filter { it.value == list1[0].value }
        println("list2=$list2")

        val list3 = list1.subtract(list2)
        println("list3=$list3")

        println(list1.subtract(list3))

        val list4 = listOf(list1[0], list1[1])
        println("list4=$list4")

        val list5 = list1.subtract(list4)
        println("list5=$list5")

        val list6 = list1.filter { !list4.contains(it) }
        println("list6=$list6")

        println(list5.indices)

        list5.elementAt(0).value=100
        println("$list5, $list1")






//        println(list.count { it != ' '})
//
//        println("[123456789]".padEnd(11, '.'))
//        println("[123]".padEnd(11, '.'))


    }


}

data class Foo(private val index: Int, var value: Int) {
    override fun hashCode() = index

    override fun toString(): String {
        return " [$index]=$value "
    }
}