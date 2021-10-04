package dev.ksyim.example.r2dbc.sharding.server

import com.linecorp.armeria.server.annotation.NullToNoContentResponseConverterFunction
import com.linecorp.armeria.spring.ArmeriaServerConfigurator
import dev.ksyim.example.r2dbc.sharding.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class ServerConfiguration {
    @Bean
    fun configureServer(userService: UserService) = ArmeriaServerConfigurator { sb -> sb
        .annotatedService()
        .responseConverters(NullToNoContentResponseConverterFunction())
        .build(userService)
    }
}
