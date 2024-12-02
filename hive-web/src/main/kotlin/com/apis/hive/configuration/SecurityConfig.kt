 package com.apis.hive.configuration

 import com.apis.hive.filter.JwtAuthenticationFilter
 import com.apis.hive.util.AppConstant
 import org.springframework.beans.factory.annotation.Autowired
 import org.springframework.context.annotation.Bean
 import org.springframework.context.annotation.Configuration
 import org.springframework.context.annotation.Primary
 import org.springframework.security.config.annotation.web.builders.HttpSecurity
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 import org.springframework.security.web.SecurityFilterChain
 import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

 @Configuration
 @EnableWebSecurity
 class SecurityConfig {


     @Autowired
     private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

     @Bean
     @Primary
     fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
         return http.csrf { it.disable() }
             .authorizeHttpRequests { auth ->
                 auth.requestMatchers(*AppConstant.PUBLIC_ENDPOINTS.toTypedArray()).permitAll()
                 auth.anyRequest().authenticated()
             }
             .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
             .formLogin { it.disable() }
             .httpBasic { it.disable() }
             .build()
     }

 }
