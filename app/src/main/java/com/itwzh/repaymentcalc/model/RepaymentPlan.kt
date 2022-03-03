package com.itwzh.repaymentcalc.model

data class RepaymentPlan(
    var repaymentAmount: Double = 0.0, //月还款额 ->由本金和利息组成
    var repaymentPrincipal: Double = 0.0,//月还本金
    var repaymentInterest: Double = 0.0,//月还利息
    var repaymentDate: String = "",//还款日期
    var surplusPrincipal: Double = 0.0, //剩余贷款金额
    var issueNumber: String = ""//期数
){
    override fun toString(): String {
        return "RepaymentPlan( 期数='$issueNumber',月还款额=$repaymentAmount, 月还本金=$repaymentPrincipal, 月还利息=$repaymentInterest, 还款日期='$repaymentDate', 剩余贷款金额=$surplusPrincipal)"
    }
}
