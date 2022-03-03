package com.itwzh.repaymentcalc.utlis

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.itwzh.repaymentcalc.model.LoanResult
import com.itwzh.repaymentcalc.model.LoanResultAdvance
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun getLoanResult(loanAmount: Double, loanTime: Int, loanRate: Double, isEP: Boolean): LoanResult {
    var result = LoanResult()
    result.loanAmount = loanAmount
    result.loanTimes = loanTime
    result.isEP = isEP
    if (isEP) result.repaymentType = "等额本金" else result.repaymentType = "等额本息"
    if (isEP) calculateEqualPrincipalAndInterest(
        loanAmount,
        loanTime,
        loanRate,
        result
    ) else calculateEqualPrincipal(loanAmount, loanTime, loanRate, result)
    return result;
}

/**
 * 计算等额本息还款
 *	等额本息
 *	每月还款本金+利息一样。
 *	贷款本金100万，贷款期限30年，贷款利息91万元（近似值），那么月供191万元/360月。
 * @param principal 贷款总额
 * @param months    贷款期限
 * @param rate      贷款利率
 * @return
 */
fun calculateEqualPrincipalAndInterest(
    principal: Double,
    months: Int,
    loanRate: Double,
    result: LoanResult
): LoanResult {
    val monthRate: Double = loanRate / (100 * 12) //月利率
    val preLoan: Double =
        principal * monthRate * Math.pow(
            1 + monthRate,
            months.toDouble()
        ) / (Math.pow(1 + monthRate, months.toDouble()) - 1) //每月还款金额

    val totalMoney: Double = preLoan * months //还款总额
    val interest: Double = totalMoney - principal //还款总利息

    result.totalAmount = totalMoney
    result.interestAmount = interest
    result.repaymentMonth = preLoan
    return result
}

/**
 * 计算等额本金还款
 * 等额本金
 * 贷款本金100万，贷款期限30年。每月还款本金=100万/360月，每月还款利息=剩余贷款金额*月利率。所以每月利息递减。
 * @param principal 贷款总额
 * @param months    贷款期限
 * @param rate      贷款利率
 * @return
 */
fun calculateEqualPrincipal(
    principal: Double,
    months: Int,
    rate: Double,
    result: LoanResult
): LoanResult {
    val monthRate = rate / (100 * 12) //月利率
    val prePrincipal = principal / months //每月还款本金
    val firstMonth = prePrincipal + principal * monthRate //第一个月还款金额
    val decreaseMonth = prePrincipal * monthRate //每月利息递减
    val interest = (months + 1) * principal * monthRate / 2 //还款总利息
    val totalMoney = principal + interest //还款总额

    result.interestAmount = interest
    result.repaymentFirstMonth = firstMonth
    result.totalAmount = totalMoney
    return result
}


fun getLoanResultAdvance(
    amount: Double,
    months: Int,
    rate: Double,
    isEP: Boolean,
    firstDate: String,
    advanceDate: String,
    advanceAmount: Double,
    isReduceTime: Boolean
): LoanResultAdvance {
    var result = LoanResultAdvance()
    result.loanAmount = amount
    result.months = months
    result.isEP = isEP
    if (isEP) result.repaymentType = "等额本金" else result.repaymentType = "等额本息"
    if (isReduceTime) result.advanceType = "缩短还款时间" else result.advanceType = "减少每月金额"
    val repaymentMonths = getRepaymentNormalMonth(firstDate, advanceDate);
    if (isReduceTime) {
        //缩短还款时间
        if (isEP) {
            //等额本金
            calculateEqualPrincipalApart(amount,months,advanceAmount,repaymentMonths-1,rate,firstDate,result)
        } else {
            //等额本息
            calculateEqualPrincipalAndInterestApart(amount,months,advanceAmount,repaymentMonths-1,rate,firstDate,result)
        }
    } else {
        //减少每月金额
        if (isEP) {
            //等额本金
            calculateEqualPrincipalAndInterestApart2(amount,months,advanceAmount,repaymentMonths-1,rate,firstDate,result)
        } else {
            //等额本息
            calculateEqualPrincipalAndInterestApart2(amount,months,advanceAmount,repaymentMonths-1,rate,firstDate,result)
        }
    }
    return result;
}


/*********************         月供不变 start      */
/**
 * 部分提前还款计算（等额本息、月供不变）
 *
 * @param principal      贷款总额
 * @param months         贷款期限
 * @param aheadPrincipal 提前还款金额
 * @param payTimes       已还次数
 * @param rate           贷款利率
 * @return
 */
fun calculateEqualPrincipalAndInterestApart(
    principal: Double,
    months: Int,
    aheadPrincipal: Double,
    payTimes: Int,
    rate: Double,
    firstDate: String,
    result: LoanResultAdvance
){
    val monthRate = rate / (100 * 12) //月利率
    val preLoan = principal * monthRate * Math.pow(1 + monthRate, months.toDouble()) / (Math.pow(
        1 + monthRate, months.toDouble()
    ) - 1) //每月还款金额
    val totalMoney = preLoan * months //还款总额
    val interest = totalMoney - principal //还款总利息
    val leftLoanBefore = principal * Math.pow(
        1 + monthRate,
        payTimes.toDouble()
    ) - preLoan * (Math.pow(1 + monthRate, payTimes.toDouble()) - 1) / monthRate //提前还款前欠银行的钱
    val leftLoan = principal * Math.pow(
        1 + monthRate,
        (payTimes + 1).toDouble()
    ) - preLoan * (Math.pow(
        1 + monthRate,
        (payTimes + 1).toDouble()
    ) - 1) / monthRate - aheadPrincipal //提前还款后欠银行的钱
    val payLoan = principal - leftLoanBefore //已还本金
    val payTotal = preLoan * payTimes //已还总金额
    val payInterest = payTotal - payLoan //已还利息
    val aheadTotalMoney = aheadPrincipal + preLoan //提前还款总额
    //计算剩余还款期限
    val leftMonth =
        Math.floor(Math.log(preLoan / (preLoan - leftLoan * monthRate)) / Math.log(1 + monthRate))
            .toInt()
    val newPreLoan =
        leftLoan * monthRate * Math.pow(1 + monthRate, leftMonth.toDouble()) / (Math.pow(
            1 + monthRate, leftMonth.toDouble()
        ) - 1) //剩余贷款每月还款金额
    val leftTotalMoney = newPreLoan * leftMonth //剩余还款总额
    val leftInterest = leftTotalMoney - (leftLoan - aheadPrincipal)
    val saveInterest = totalMoney - aheadTotalMoney - leftTotalMoney - payTotal
    result.loanAmount = principal
    result.totalAmount = totalMoney
    result.advanceAmount = leftTotalMoney
    result.interestTotal = interest
    result.advanceInterestTotal = leftInterest
    result.repaymentMonth = preLoan
    result.advanceRepaymentMonth = newPreLoan
    result.reduceMonths = months-leftMonth - payTimes
    result.endDate = getEndDate(firstDate,leftMonth + payTimes)
    result.reduceInterest = saveInterest
    result.repaidTotal = payTotal
    result.repaidAmount = payLoan
    result.repaidInterest = payInterest
    result.advanceTotalMoney = aheadTotalMoney
}

/**
 * 部分提前还款计算(等额本金、月供不变)
 * @param principal      贷款总额
 * @param months         贷款期限
 * @param aheadPrincipal 提前还款金额
 * @param payTimes       已还次数
 * @param rate           贷款利率
 * @return
 */
fun calculateEqualPrincipalApart(
    principal: Double,
    months: Int,
    aheadPrincipal: Double,
    payTimes: Int,
    rate: Double,
    firstDate: String,
    result: LoanResultAdvance
) {
    val monthRate = rate / (100 * 12) //月利率
    val prePrincipal = principal / months //每月还款本金
    val firstMonth = prePrincipal + principal * monthRate //第一个月还款金额
    val interest = (months + 1) * principal * monthRate / 2 //还款总利息
    val totalMoney = principal + interest //还款总额
    val payLoan = prePrincipal * payTimes //已还本金
    val payInterest =
        (principal * payTimes - prePrincipal * (payTimes - 1) * payTimes / 2) * monthRate //已还利息
    val payTotal = payLoan + payInterest //已还总额
    val aheadTotalMoney = (principal - payLoan) * monthRate + aheadPrincipal + prePrincipal //提前还款金额
    val leftLoan = principal - aheadPrincipal - payLoan - prePrincipal //剩余金额
    val leftMonth = Math.floor(leftLoan / prePrincipal).toInt()
    val newPrePrincipal = leftLoan / leftMonth //新的每月还款本金
    val newFirstMonth = newPrePrincipal + leftLoan * monthRate //新的第一个月还款金额
    val leftInterest = (leftMonth + 1) * leftLoan * monthRate / 2 //还款总利息
    val leftTotalMoney = leftLoan + leftInterest //还款总额
    val saveInterest = totalMoney - payTotal - aheadTotalMoney - leftTotalMoney
    result.loanAmount = principal
    result.totalAmount = totalMoney
    result.advanceAmount = leftTotalMoney
    result.interestTotal = interest
    result.advanceInterestTotal = leftInterest
    result.repaymentFirstMonth = firstMonth
    result.advanceRepaymentFirstMonth = newFirstMonth
    result.reduceMonths = months-leftMonth - payTimes
    result.endDate = getEndDate(firstDate,leftMonth + payTimes)
    result.reduceInterest = saveInterest
    result.repaidTotal = payTotal
    result.repaidAmount = payLoan
    result.repaidInterest = payInterest
    result.advanceTotalMoney = aheadTotalMoney
}

/*********************         月供不变 	 end     ***********************/



/*********************         期限不变		 start     */
/**
 * 部分提前还款计算（等额本息、期限不变）
 * @param principal      贷款总额
 * @param months         贷款期限
 * @param aheadPrincipal 提前还款金额
 * @param payTimes       已还次数
 * @param rate           贷款利率
 * @return
 */
fun calculateEqualPrincipalAndInterestApart2(
    principal: Double,
    months: Int,
    aheadPrincipal: Double,
    payTimes: Int,
    rate: Double,
    firstDate: String,
    result: LoanResultAdvance
){
    val monthRate = rate / (100 * 12) //月利率
    val preLoan = principal * monthRate * Math.pow(1 + monthRate, months.toDouble()) / (Math.pow(
        1 + monthRate, months.toDouble()
    ) - 1) //每月还款金额
    val totalMoney = preLoan * months //还款总额
    val interest = totalMoney - principal //还款总利息
    val leftLoanBefore = principal * Math.pow(
        1 + monthRate,
        payTimes.toDouble()
    ) - preLoan * (Math.pow(1 + monthRate, payTimes.toDouble()) - 1) / monthRate //提前还款前欠银行的钱
    val leftLoan = principal * Math.pow(
        1 + monthRate,
        (payTimes + 1).toDouble()
    ) - preLoan * (Math.pow(1 + monthRate, (payTimes + 1).toDouble()) - 1) / monthRate //提前还款后银行的钱
    val payLoan = principal - leftLoanBefore //已还本金
    val payTotal = preLoan * payTimes //已还总金额
    val payInterest = payTotal - payLoan //已还利息
    val aheadTotalMoney = preLoan + aheadPrincipal //下个月还款金额
    val newPreLoan = (leftLoan - aheadPrincipal) * monthRate * Math.pow(
        1 + monthRate,
        (months - payTimes - 1).toDouble()
    ) / (Math.pow(
        1 + monthRate, (months - payTimes - 1).toDouble()
    ) - 1) //下个月起每月还款金额
    val leftTotalMoney = newPreLoan * (months - payTimes)
    val leftInterest = leftTotalMoney - (leftLoan - aheadPrincipal)
    val saveInterest = totalMoney - payTotal - aheadTotalMoney - leftTotalMoney
    result.loanAmount = principal
    result.totalAmount = totalMoney
    result.advanceAmount = leftTotalMoney
    result.interestTotal = interest
    result.advanceInterestTotal = leftInterest
    result.repaymentMonth = preLoan
    result.advanceRepaymentMonth = newPreLoan
    result.endDate = getEndDate(firstDate,months)
    result.reduceInterest = saveInterest
    result.repaidTotal = payTotal;
    result.repaidAmount = payLoan
    result.repaidInterest = payInterest
    result.advanceTotalMoney = aheadTotalMoney
}

/**
 * 部分提前还款计算(等额本金、期限不变)
 * @param principal      贷款总额
 * @param months         贷款期限
 * @param aheadPrincipal 提前还款金额
 * @param payTimes       已还次数
 * @param rate           贷款利率
 * @return
 */
fun calculateEqualPrincipalApart2(
    principal: Double,
    months: Int,
    aheadPrincipal: Double,
    payTimes: Int,
    rate: Double,
    firstDate: String,
    result: LoanResultAdvance
){
    val monthRate = rate / (100 * 12) //月利率
    val prePrincipal = principal / months //每月还款本金
    val firstMonth = prePrincipal + principal * monthRate //第一个月还款金额
    val decreaseMonth = prePrincipal * monthRate //每月利息递减
    val interest = (months + 1) * principal * monthRate / 2 //还款总利息
    val totalMoney = principal + interest //还款总额
    val payLoan = prePrincipal * payTimes //已还本金
    val payInterest =
        (principal * payTimes - prePrincipal * (payTimes - 1) * payTimes / 2) * monthRate //已还利息
    val payTotal = payLoan + payInterest //已还总额
    val aheadTotalMoney = (principal - payLoan) * monthRate + aheadPrincipal + prePrincipal //提前还款金额
    val leftMonth = months - payTimes - 1
    val leftLoan = principal - aheadPrincipal - payLoan - prePrincipal
    val newPrePrincipal = leftLoan / leftMonth //新的每月还款本金
    val newFirstMonth = newPrePrincipal + leftLoan * monthRate //新的第一个月还款金额
    val newDecreaseMonth = newPrePrincipal * monthRate //新的每月利息递减
    val leftInterest = (leftMonth + 1) * leftLoan * monthRate / 2 //还款总利息
    val leftTotalMoney = leftLoan + leftInterest //还款总额
    val saveInterest = totalMoney - payTotal - aheadTotalMoney - leftTotalMoney
    result.loanAmount = principal
    result.totalAmount = totalMoney
    result.advanceAmount = leftTotalMoney
    result.interestTotal = interest
    result.advanceInterestTotal = leftInterest
    result.repaymentFirstMonth = firstMonth
    result.advanceRepaymentFirstMonth = newFirstMonth
    result.reduceMonths = months-leftMonth - payTimes
    result.endDate = getEndDate(firstDate,leftMonth + payTimes)
    result.reduceInterest = saveInterest
    result.repaidTotal = payTotal
    result.repaidAmount = payLoan
    result.repaidInterest = payInterest
    result.advanceTotalMoney = aheadTotalMoney
}
/*********************         期限不变		 end    **********************/

/**
 * 格式化 double 保留两位小数
 * @author luffy
 */
fun format(totalMoney: Double): String? {
    val df = DecimalFormat("#.00")
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
    // 指定格式化格式
    val sdf = SimpleDateFormat("yyyy年MM月dd日")
    return sdf.format(now)
}

fun getEndDate(firstDate:String,months:Int):String{
    var yearFirst = firstDate.substring(0, 4).toInt()
    var monthFirst = firstDate.substring(6, 7).toInt()
    var day = firstDate.substring(9, 10).toInt()
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