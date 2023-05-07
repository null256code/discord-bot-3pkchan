package spkchan.domain.models.finance

import java.time.LocalDateTime

class Income(
    recordId: Long,
    transactionDateTime: LocalDateTime,
    amount: Int,
    place: String,
    comment: String,
    val category: IncomeCategory,
) : FinancialRecord(
    recordId,
    transactionDateTime,
    amount,
    place,
    comment,
)

enum class IncomeCategory(val id: Long) {
    Salary(11),
    AdvancesRepayment(12),
    Bonus(13),
    ExtraordinaryRevenue(14),
    BusinessIncome(15),
    Other(19),
    ;

    companion object {
        fun valueOf(id: Long) = values().first { it.id == id }
    }
}
