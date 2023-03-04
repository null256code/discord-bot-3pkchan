package spkchan.external.apis.rakuten

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.util.*

typealias Endpoint = String

@Component
class RakutenRecipeApiClient(
    @Value("\${botconfig.rakuten.application_id}") val applicationId: String,
    val restTemplate: RestTemplate,
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

        val result = kotlin.runCatching {
            restTemplate.getForEntity(
                ENDPOINT.setup(categories.map { it.categoryId }),
                RakutenRecipeCategoryRankingResponse::class.java,
            )
        }

        return when {
            result.isSuccess -> {
                logger.info("fetchCategoryRanking() is finished.")
                result.map {
                    if (it.statusCode != HttpStatus.OK) {
                        null
                    } else {
                        it.body?.apply { fetchedDateTime = LocalDateTime.now() }
                    }
                }.getOrNull()
            }
            else -> {
                logger.warn("fetchCategoryRanking() is failed.")
                logger.warn(result.toString())
                null // TODO: Responseの結果型を作るべき
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
