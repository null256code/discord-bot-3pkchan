package spkchan

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableCaching
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}

@Configuration
class AppConfig {
    @Bean
    fun restTemplate(): RestTemplate = RestTemplateBuilder()
        .messageConverters(GsonHttpMessageConverter())
        .build()

    @Bean
    fun gson(): Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
}
