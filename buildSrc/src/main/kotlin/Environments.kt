object Environments {
    object DataSource {
        val isLocal = System.getenv("SPRING_DATASOURCE_URL") == null
        val url = System.getenv("SPRING_DATASOURCE_URL") ?: "jdbc:postgresql://localhost:5432/postgres"
        val username = System.getenv("SPRING_DATASOURCE_USERNAME") ?: "postgres"
        val password = System.getenv("SPRING_DATASOURCE_PASSWORD") ?: "postgres"
    }
}