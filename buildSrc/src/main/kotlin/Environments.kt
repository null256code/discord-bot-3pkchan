object Environments {
    // できればapplication.ymlの定義そのまま使いたいが…
    object DataSource {
        val url = System.getenv("SPRING_DATASOURCE_URL") ?: "jdbc:postgresql://localhost:5432/postgres"
        val username = System.getenv("SPRING_DATASOURCE_USERNAME") ?: "postgres"
        val password = System.getenv("SPRING_DATASOURCE_PASSWORD") ?: "postgres"
    }
}