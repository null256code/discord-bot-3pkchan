package spkchan.domain.models.finance

import java.time.LocalDateTime

abstract class FinancialRecord(
    val recordId: Long,
    val transactionDateTime: LocalDateTime,
    val amount: Int,
    val place: String,
    val comment: String,
)
