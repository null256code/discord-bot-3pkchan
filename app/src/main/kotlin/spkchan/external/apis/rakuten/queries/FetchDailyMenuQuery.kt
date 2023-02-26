package spkchan.external.apis.rakuten.queries

import org.springframework.stereotype.Component
import spkchan.external.apis.rakuten.RakutenRecipeApiClient
import spkchan.external.apis.rakuten.RakutenRecipeCategory
import java.time.LocalDate

@Component
class FetchDailyMenuQuery(
    private val rakutenRecipeApiClient: RakutenRecipeApiClient,
) {
    fun handle(): FindDailyMenuQueryResult {
        val param = listOf(RakutenRecipeCategory.CategoryL.DAILY_MENU)
        var ranking = rakutenRecipeApiClient.fetchCategoryRanking(param) ?: return FindDailyMenuQueryResult(emptyList())

        // TODO: キャッシュクリアするかチェック共通化したい
        if (LocalDate.now().isAfter(ranking.fetchedDateTime.toLocalDate())) {
            rakutenRecipeApiClient.clearCacheOfCategoryRanking(param)
            ranking = rakutenRecipeApiClient.fetchCategoryRanking(param) ?: return FindDailyMenuQueryResult(emptyList())
        }

        return ranking.result.map {
            FindDailyMenuQueryResult.Recipe(
                title = it.recipeTitle,
                recipeUrl = it.recipeUrl,
                imageUrl = it.mediumImageUrl,
                recipeDescription = it.recipeDescription.let { text ->
                    if (it.recipeDescription.length > 30) {
                        "${text.substring(0..30)}..."
                    } else {
                        text
                    }
                },
                ingredientsDescription = it.recipeMaterial.joinToString(", "),
                rank = kotlin.runCatching { it.rank.toInt() }.getOrDefault(0),
            )
        }.let { FindDailyMenuQueryResult(it) }
    }
}

data class FindDailyMenuQueryResult(
    val recipes: List<Recipe>,
) {
    data class Recipe(
        val title: String,
        val recipeUrl: String,
        val imageUrl: String,
        val recipeDescription: String,
        val ingredientsDescription: String,
        val rank: Int,
    )
}
