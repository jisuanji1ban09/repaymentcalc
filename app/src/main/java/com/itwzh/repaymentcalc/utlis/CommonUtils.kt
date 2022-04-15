package com.itwzh.repaymentcalc.utlis

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * 格式化 double 保留两位小数
 * @author luffy
 */
fun format(totalMoney: Double): String? {
    val df = DecimalFormat("#.00")
    if (df.format(totalMoney).equals(".00")){
        return "0.00"
    }
    return df.format(totalMoney)
}

fun hideSoftKeyboard(context: Context, view: View){
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    //隐藏软键盘
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
//    //显示软键盘
//    imm.showSoftInputFromInputMethod(view.getWindowToken(), 0)
//    //切换软键盘的显示与隐藏
//    imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS)

}

fun dateFormat(millisecond:Long):String{
    val now = Date(millisecond) // 创建一个Date对象，获取当前时间

    // 指定格式化格式
    val sdf = SimpleDateFormat("yyyy年MM月dd日")
    return sdf.format(now)
}

fun advanceDateFormat(millisecond:Long,firstDate: String):String{
    val now = Date(millisecond) // 创建一个Date对象，获取当前时间

    // 指定格式化格式
    val sdf = SimpleDateFormat("yyyy年MM月dd日")
    return sdf.format(now).substring(0,8)+firstDate.substring(8,11)
}

fun dateParse(date:String):Long{

    // 指定格式化格式
    val sdf = SimpleDateFormat("yyyy年MM月dd日")
    return sdf.parse(date).time
}

fun getEndDate(firstDate:String,months:Int):String{
    var yearFirst = firstDate.substring(0, 4).toInt()
    var monthFirst = firstDate.substring(5, 7).toInt()
    var day = firstDate.substring(8, 10).toInt()
    var year:Int = 0
    var month:Int = 0
    if ((months%12+monthFirst)>12){
        month = (months%12+monthFirst)-12
        year = yearFirst+(months/12)+1
    }else{
        month = months%12+monthFirst
        year = yearFirst+(months/12)
    }
    return "${year}年${if (month < 10) "0" + month else month.toString()}月${if (day < 10) "0" + day else day.toString()}日"
}

fun getNextMonth(millisecond:Long):Long{
    if (millisecond>System.currentTimeMillis()){
        return dateParse(getEndDate(dateFormat(millisecond),1))
    }else{
        val nowDay = dateFormat(System.currentTimeMillis()).substring(8, 10).toInt()
        val firstDay = dateFormat(millisecond).substring(8, 10).toInt()
        if (nowDay>=firstDay){
            val nextDay = getEndDate(dateFormat(System.currentTimeMillis()), 1)
            val endDay = nextDay.substring(0,8)+"01日"
            return dateParse(endDay)
        }else{
            return System.currentTimeMillis()
        }
    }
}

fun getMinMonthByChoose():Long{
    return dateParse(getEndDate(dateFormat(System.currentTimeMillis()),-1200))
}

fun getMaxMonthByChoose():Long{
    return dateParse(getEndDate(dateFormat(System.currentTimeMillis()),1200))
}

fun getAdvanceMaxMonth(firstDate:Long,months: Int):Long{
    return dateParse(getEndDate(dateFormat(firstDate),months))
}
