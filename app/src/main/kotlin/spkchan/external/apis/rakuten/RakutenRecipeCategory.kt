package spkchan.external.apis.rakuten

// HACK: classとenumの両方参照できるようにinterface作成したが、外から見ると classとinterfaceどちら使えばいいか分からない
interface RakutenRecipeCategoryParameter {
    val categoryName: String
    val categoryId: Int
    val parent: RakutenRecipeCategory?
}

class RakutenRecipeCategory(
    override val categoryName: String,
    override val categoryId: Int,
    override val parent: RakutenRecipeCategory?,
) : RakutenRecipeCategoryParameter {

    enum class CategoryL(
        override val categoryName: String,
        override val categoryId: Int,
        override val parent: RakutenRecipeCategory? = null,
    ) : RakutenRecipeCategoryParameter {
        POPULAR("人気メニュー", 30),
        REGULAR_MEAT("定番の肉料理", 31),
        REGULAR_FISH("定番の魚料理", 32),
        REGULAR_EGG("卵料理", 33),
        RICE("ご飯もの", 14),
        PASTA("パスタ", 15),
        NOODLE("麺・粉物料理", 16),
        SOUP("汁物・スープ", 17),
        JAPANESE_NABE("鍋料理", 23),
        SALAD("サラダ", 18),
        BREAD("パン", 22),
        SWEETS("お菓子", 21),
        MEAT("肉", 10),
        FISH("魚", 11),
        VEGETABLE("野菜", 12),
        FRUITS("果物", 34),
        SOURCE("ソース・調味料・ドレッシング", 19),
        JUICE("飲みもの", 27),
        SOY("大豆・豆腐", 35),
        OTHER("その他の食材", 13),
        LUNCHBOX("お弁当", 20),
        SIMPLE_MENU("簡単料理・時短", 36),
        MINIMAL_BUDGET("節約料理", 37),
        DAILY_MENU("今日の献立", 38),
        HEALTHY("健康料理", 39),
        TOOL("調理器具", 40),
        OTHER_PURPOSE("その他の目的・シーン", 26),
        CHINESE("中華料理", 41),
        KOREAN("韓国料理", 42),
        ITALY("イタリア料理", 43),
        FRENCH("フランス料理", 44),
        WESTERN("西洋料理", 25),
        ETHNIC("エスニック料理・中南米", 46),
        UCHINA("沖縄料理", 47),
        GOHDO("日本各地の郷土料理", 48),
        EVENT("行事・イベント", 24),
        OSECHI("おせち料理", 49),
        XMAS("クリスマス", 50),
        HINAMATSURI("ひな祭り", 51),
        SEASON_SPRING("春（3月～5月）", 52),
        SEASON_SUMMER("夏（6月～8月）", 53),
        SEASON_AUTUMN("秋（9月～11月）", 54),
        SEASON_SINTER("冬（12月～2月）", 55),
    }
}
