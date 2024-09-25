//package cloud.catfish.security.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.web.util.matcher.RequestMatcher;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class WebSecurityConfig {
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests().requestMatchers(permittedAntMatchers()).permitAll();
//        http.authorizeHttpRequests().requestMatchers(AntPathRequestMatcher.antMatcher("/**")).authenticated();
//        http.exceptionHandling();
//        return http.build();
//    }
//
//    RequestMatcher[] permittedAntMatchers() {
//        List<RequestMatcher> matchers = new ArrayList<>();
//        matchers.add(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/users/*")); // Sample
//        matchers.add(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/index.html")); // Sample
//        matchers.add(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/index2.html")); // Sample
//        matchers.add(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/favicon.ico")); // Sample
//        matchers.add(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/css/main.css")); // Sample
//        matchers.add(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/js/main.js")); // Sample
//        matchers.add(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/ws/**")); // Sample
//        matchers.add(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/favicon.ico")); // Sample
//        // Add all your matchers
//        return matchers.toArray(new RequestMatcher[0]);
//    }
//
//}