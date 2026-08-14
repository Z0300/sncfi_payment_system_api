package com.sncfi.payment_system.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer


@Configuration
class PaginationConfig {

    @Bean
    fun pageableCustomizer(): PageableHandlerMethodArgumentResolverCustomizer =
        PageableHandlerMethodArgumentResolverCustomizer { resolver ->
            resolver.setMaxPageSize(100)
            resolver.setOneIndexedParameters(false)
        }
}