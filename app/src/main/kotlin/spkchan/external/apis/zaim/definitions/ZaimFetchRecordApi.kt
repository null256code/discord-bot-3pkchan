package spkchan.external.apis.zaim.definitions

import spkchan.external.apis.ApiRequest

data class ZaimFetchRecordRequest(
    val mapping: Int = 1, // required. set 1
    val category_id: Long? = null,
    val genre_id: Long? = null,
    val mode: String? = null,
    val order: String = "date",
    val start_date: String? = null,
    val end_date: String? = null,
    val page: Int = 1,
    val limit: Int = 20, // (default 20, max 100)
    val group_by: Long? = null, //  if you set as "receipt_id", Zaim makes the response group by the receipt_id (option)
) : ApiRequest

data class ZaimFetchRecordResponse(
    val requested: Long,
    val money: List<Money>,
) {
    data class Money(
        val id: Long,
        val userId: Long,
        val date: String,
        val mode: String,
        val categoryId: Long,
        val genreId: Long,
        val fromAccountId: Long,
        val toAccountId: Long,
        val amount: Int,
        val comment: String,
        val active: Int,
        val created: String,
        val currencyCode: String,
        val name: String,
        val receiptId: Long,
        val place: String,
    ) {
        companion object {
            const val MODE_INCOME = "income"
            const val MODE_PAYMENT = "payment"
        }
    }
}
