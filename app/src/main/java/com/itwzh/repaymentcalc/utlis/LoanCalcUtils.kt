package com.itwzh.repaymentcalc.utlis

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.itwzh.repaymentcalc.model.LoanResult
import com.itwzh.repaymentcalc.model.LoanResultAdvance
import com.itwzh.repaymentcalc.model.RepaymentPlan
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun getLoanResult(loanAmount: Double, loanTime: Int, loanRate: Double, isEP: Boolean,date:String): LoanResult {
    var result = LoanResult()
    result.loanAmount = loanAmount
    result.loanTimes = loanTime
    result.isEP = isEP
    result.repaymentLastDate = getRepaymentDate(date,loanTime)
    if (isEP) result.repaymentType = "等额本金" else result.repaymentType = "等额本息"
    val repaymentPlan = getRepaymentPlan(
        amount = loanAmount,
        months = loanTime,
        rate = loanRate,
        date = date,
        isEP = isEP
    )
    if (!isEP) calculateEqualPrincipalAndInterest(
        loanAmount,
        loanTime,
        loanRate,
        result,
        repaymentPlan
    ) else calculateEqualPrincipal(loanAmount, loanTime, loanRate, result,repaymentPlan)
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
    result: LoanResult,
    repaymentPlan: List<RepaymentPlan>
): LoanResult {

    var insetTotal = 0.0

    for (issue in 0..repaymentPlan.size-1){
        insetTotal += repaymentPlan.get(issue).repaymentInterest
    }


    val monthRate: Double = loanRate / (100 * 12) //月利率
    val preLoan: Double =
        principal * monthRate * Math.pow(
            1 + monthRate,
            months.toDouble()
        ) / (Math.pow(1 + monthRate, months.toDouble()) - 1) //每月还款金额

    val totalMoney: Double = principal + insetTotal //还款总额

    result.totalAmount = totalMoney
    result.interestAmount = insetTotal
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
    result: LoanResult,
    repaymentPlan: List<RepaymentPlan>
): LoanResult {
    val monthRate = rate / (100 * 12) //月利率
    val prePrincipal = principal / months //每月还款本金
    val firstMonth = prePrincipal + principal * monthRate //第一个月还款金额
    var insetTotal = 0.0

    for (issue in 0..repaymentPlan.size-1){
        insetTotal += repaymentPlan.get(issue).repaymentInterest
    }
    val totalMoney = principal + insetTotal //还款总额



    result.interestAmount = insetTotal
    result.repaymentFirstMonth = firstMonth
    result.totalAmount = totalMoney
    return result
}


