package spkchan.external.apis.zaim.definitions

data class ZaimVerifyUserResponse(
    val me: Me,
    val requested: Long,
) {
    data class Me(
        val login: String,
        val inputCount: Int,
        val dayCount: Int,
        val repeatCount: Int,
        val id: Long,
        val currencyCode: String,
        val week: Int,
        val month: Int,
        val active: Int,
        val day: Int,
        val profileModified: String,
        val name: String,
        val created: String,
        val profileImageUrl: String,
        val coverImageUrl: String,
    )
}
