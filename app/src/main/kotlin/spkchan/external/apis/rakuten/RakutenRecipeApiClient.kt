package spkchan.external.apis.rakuten

import com.github.kittinunf.fuel.gson.gsonDeserializer
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

typealias Endpoint = String

@Component
class RakutenRecipeApiClient(
    @Value("\${botconfig.rakuten.application_id}") val applicationId: String,
) {
    companion object {
        private const val ENDPOINT: Endpoint = "https://app.rakuten.co.jp/services/api/Recipe/CategoryRanking/20170426"
        private const val CACHE_VALUE = "fetchCategoryRanking"
        private val logger = LoggerFactory.getLogger(RakutenRecipeApiClient::class.java)
    }

    private fun Endpoint.setup(categoryIds: List<Int>) =
        "$this?applicationId=$applicationId&categoryId=${categoryIds.joinToString("-")}"

    @Cacheable(
        value = [CACHE_VALUE],
        key = "'$CACHE_VALUE/' + #categories.![categoryId].toString()",
    )
    fun fetchCategoryRanking(categories: List<RakutenRecipeCategoryParameter>): RakutenRecipeCategoryRankingResponse? {
        logger.info("fetchCategoryRanking() is called.")
        val (_, _, result) = ENDPOINT.setup(categories.map { it.categoryId })
            .httpGet()
            .responseObject<RakutenRecipeCategoryRankingResponse>(gsonDeserializer())

        return when (result) {
            is Result.Failure -> {
                logger.warn("fetchCategoryRanking() is failed.")
                logger.warn(result.toString())
                null // TODO: Responseの結果型を作るべき
            }
            is Result.Success -> {
                logger.info("fetchCategoryRanking() is finished.")
                result.value.apply { fetchedDateTime = LocalDateTime.now() }
            }
        }
    }

    @CacheEvict(
        value = [CACHE_VALUE],
        key = "'$CACHE_VALUE/' + #categories.![categoryId].toString()",
    )
    fun clearCacheOfCategoryRanking(categories: List<RakutenRecipeCategoryParameter>) = logger.info("clearCacheOfCategoryRanking() is called.")
}

// https://webservice.rakuten.co.jp/documentation/recipe-category-ranking
data class RakutenRecipeCategoryRankingResponse(
    var result: List<RankingRecipe>,
    @Transient var fetchedDateTime: LocalDateTime,
) {
    data class RankingRecipe(
        var recipeId: Long,
        var recipeTitle: String,
        var recipeUrl: String,
        var foodImageUrl: String,
        var mediumImageUrl: String,
        var smallImageUrl: String,
        var pickup: Int,
        var shop: Int,
        var nickname: String,
        var recipeDescription: String,
        var recipeMaterial: List<String>,
        var recipeIndication: String,
        var recipeCost: String,
        // var recipePublishday: String,
        var rank: String,
    )
}

class GsonDeserializer : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH::mm::ss")

        fun create(): Gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, GsonDeserializer)
            .setPrettyPrinting()
            .create()
    }

    override fun serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        JsonPrimitive(dateTimeFormatter.format(src))

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime =
        LocalDateTime.parse(
            json.asString,
            DateTimeFormatter.ofPattern("uuuu/MM/dd HH::mm::ss").withLocale(Locale.JAPAN),
        )
}
