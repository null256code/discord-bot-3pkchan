package spkchan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
