package com.itwzh.repaymentcalc.model

data class LoanResult(
    var totalAmount: Double = 0.0,
    var loanAmount: Double = 0.0,
    var interestAmount: Double = 0.0,
    var loanTimes: Int = 0,
    var repaymentMonth: Double = 0.0,
    var repaymentFirstMonth: Double = 0.0,
    var repaymentType: String = "",
    var isEP :Boolean = false
) {

}
