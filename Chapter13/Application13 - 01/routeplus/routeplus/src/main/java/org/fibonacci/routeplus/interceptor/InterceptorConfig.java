package org.fibonacci.routeplus.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author k
 * @date 2019-07-08
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /**
     * 不需要登录拦截的url
     */
    final String[] notLoginInterceptPaths = {
            "/home",
            "/user/login",
            "/error",
            "/cmp_sp/**",
            "/slowSqlIncrement"
            ,"/metrics"
            ,"/routes/deploy/kong"
            ,"/routes/remove/kong"
            ,"/routes/get/target"
            ,"/routes/check/status"
            ,"/routes/update/route"
    };

    @Bean
    public HandlerInterceptor getLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(getLoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns(notLoginInterceptPaths);
    }

}
