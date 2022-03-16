package com.itwzh.repaymentcalc.utlis

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.itwzh.repaymentcalc.model.LoanResult
import com.itwzh.repaymentcalc.model.LoanResultAdvance
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
    if (!isEP) calculateEqualPrincipalAndInterest(
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


