package com.itwzh.repaymentcalc.utlis

import com.itwzh.repaymentcalc.model.LoanResultFullAmount

fun getLoanResultFullAmount(
    amount: Double,
    months: Int,
    rate: Double,
    isEP: Boolean,
    firstDate: String,
    advanceDate: String,
): LoanResultFullAmount {
    val result = LoanResultFullAmount()
    result.loanAmount = amount
    result.loanTimes = months
    result.isEP = isEP
    result.repaymentLastDate = getRepaymentDate(firstDate, months)
    //已还期数
    val repaymentNormalMonth = getRepaymentNormalMonth(firstDate, advanceDate)
    result.repaymentNormalMonth = repaymentNormalMonth;
    if (isEP) result.repaymentType = "等额本金" else result.repaymentType = "等额本息"
    if (isEP) {
        calcEPLoan(amount, months, rate, repaymentNormalMonth, result)
    } else {
        calcEPAILoan(amount, months, rate, repaymentNormalMonth, result)
    }
    return result
}

/**
 * 等额本息
 */
fun calcEPAILoan(
    principal: Double,
    months: Int,
    rate: Double,
    payTimes: Int,
    result: LoanResultFullAmount
) {
    val monthRate: Double = rate / (100 * 12) //月利率
    val preLoan: Double =
        principal * monthRate * Math.pow(
            1 + monthRate,
            months.toDouble()
        ) / (Math.pow(1 + monthRate, months.toDouble()) - 1) //每月还款金额

    val totalMoney: Double = preLoan * months //还款总额
    val interest: Double = totalMoney - principal //还款总利息
    val leftLoanBefore = principal * Math.pow(
        1 + monthRate,
        payTimes.toDouble()
    ) - preLoan * (Math.pow(1 + monthRate, payTimes.toDouble()) - 1) / monthRate //提前还款前欠银行的钱

    result.totalAmount = totalMoney
    result.interestAmount = interest
    result.repaymentMonth = preLoan
    val payLoan = principal - leftLoanBefore //已还本金
    result.haveRepaymentAmount = payLoan
    val payTotal = preLoan * payTimes //已还总金额
    result.haveTotalAmount = payTotal
    result.haveInterestAmount = payTotal - payLoan
    result.surplusAmount = principal - payLoan
    val aheadInterest = result.surplusAmount * monthRate
    val aheadRepaymentAmount = aheadInterest +result.surplusAmount  // 一次还款额
    result.aheadRepaymentAmount = aheadRepaymentAmount
    result.aheadInterest = aheadInterest
    //节省利息
    val saveInterest = interest -     result.haveInterestAmount - aheadInterest
    result.saveInterest = saveInterest
}


/**
 * 等额本金
 */
fun calcEPLoan(
    principal: Double,
    months: Int,
    rate: Double,
    repaymentNormalMonth: Int,
    result: LoanResultFullAmount
) {
    val monthRate = rate / (100 * 12) //月利率
    val prePrincipal = principal / months //每月还款本金
    val firstMonth = prePrincipal + principal * monthRate //第一个月还款金额
    val decreaseMonth = prePrincipal * monthRate //每月利息递减
    val interest = (months + 1) * principal * monthRate / 2 //还款总利息
    val totalMoney = principal + interest //还款总额

    result.interestAmount = interest
    result.repaymentFirstMonth = firstMonth
    result.totalAmount = totalMoney

    //已还本金
    result.haveRepaymentAmount = prePrincipal * repaymentNormalMonth
    //已还利息

    //剩余本金
    val surplusAmount = principal - result.haveRepaymentAmount


}
