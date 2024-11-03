// package com.apis.hive.configuration
//
// import com.apis.hive.util.AppConstant
// import org.springframework.context.annotation.Bean
// import org.springframework.context.annotation.Configuration
// import org.springframework.context.annotation.Primary
// import org.springframework.security.config.annotation.web.builders.HttpSecurity
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
// import org.springframework.security.web.SecurityFilterChain
//
// //@Configuration
// //@EnableWebSecurity
// //class SecurityConfig {
// //    @Bean
// //    @Primary
// //    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
// //        return http.csrf { it.disable() }
// //            .authorizeRequests {
// //                it.requestMatchers(*AppConstant.PUBLIC_ENDPOINTS.toTypedArray()).permitAll()
// //            }
// //            .authorizeRequests{
// //                it.anyRequest().authenticated()
// //            }
// //            .formLogin { it.disable() }
// //            .httpBasic { it.disable() }
// //            .build()
// //    }
// //
// //}
