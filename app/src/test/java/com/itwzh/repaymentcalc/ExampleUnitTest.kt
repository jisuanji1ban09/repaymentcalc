package com.itwzh.repaymentcalc

import com.itwzh.repaymentcalc.utlis.*
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        println(getRepaymentDate("2022年11月15日",240))
//        println("-0.00".toDouble())
//        var R = 3129.9
//        var p = 5.635/(100*12)
//        val Q = 384236.62
//        val i = Math.log(R/(R-Q*p))/Math.log(1+p)
//        val j = Math.ceil(i).toInt()
//        println("i is $i")
//        println("j is $j")
//        val time = getNextMonth(1647416412503L)
//        println(time.toString())
//        println(dateFormat(time))
//        val  time = 1649952000000L;
//        println(dateFormat(time))
//        println(advanceDateFormat(time,"2021年02月01日"))
//        println(dateFormat(getNextMonth(dateParse("2022年03月19日"))))
//        println(dateFormat(getMinMonthByChoose()))
//        println(dateFormat(getMaxMonthByChoose()))
        println(getNaturalRepaymentMonth("2021年03月01日","2022年05月01日"))
    }
}

fun getRepaymentDate(date: String, issue: Int): String {
    var year = date.substring(0, 4).toInt()
    var month = date.substring(5, 7).toInt()
    val day = date.substring(8, 10).toInt()
    println("year is "+year+" month is "+month+" day is "+day)
    year += (month + issue - 1) / 12
    month = (month + (issue - 1)) % 12
    return "${year}年${if (month < 10) "0" + month else month.toString()}月${if (day < 10) "0" + day else day.toString()}日"
}