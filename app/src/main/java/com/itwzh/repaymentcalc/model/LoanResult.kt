package com.itwzh.repaymentcalc.model

data class LoanResult(
    var totalAmount: Double = 0.0, //总还款额
    var loanAmount: Double = 0.0, //贷款总额
    var interestAmount: Double = 0.0, //利息总额
    var loanTimes: Int = 0, //贷款期限
    var repaymentMonth: Double = 0.0, //月还款
    var repaymentFirstMonth: Double = 0.0,//首月还款
    var repaymentType: String = "", //还款方式
    var repaymentLastDate:String="",//最后还款日期
    var isEP :Boolean = false, //是否 是->等额本金 否->等额本息
) {

}
