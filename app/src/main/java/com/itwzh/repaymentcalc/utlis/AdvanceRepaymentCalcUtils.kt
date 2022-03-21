package com.itwzh.repaymentcalc.utlis

import com.itwzh.repaymentcalc.model.LoanResultAdvance

fun getLoanResultAdvance(
    amount: Double,
    months: Int,
    rate: Double,
    isEP: Boolean,
    firstDate: String,
    advanceDate: String,
    advanceAmount: Double,
    isAdvance: Boolean
): LoanResultAdvance {
    var result = LoanResultAdvance()
    result.loanAmount = amount
    result.months = months
    result.isEP = isEP
    result.repaymentLastDate = getEndDate(firstDate, months)
    if (isEP) result.repaymentType = "等额本金" else result.repaymentType = "等额本息"
    if (isAdvance) result.advanceType = "缩短还款时间" else result.advanceType = "减少每月金额"
    val repaymentMonths = getRepaymentNormalMonth(firstDate, advanceDate);
    if (isAdvance) {
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
    result.reduceInterest = saveInterest
    result.repaidTotal = payTotal
    result.repaidAmount = payLoan
    result.repaidInterest = payInterest
    result.advanceTotalMoney = aheadTotalMoney
}
/*********************         期限不变		 end    **********************/
