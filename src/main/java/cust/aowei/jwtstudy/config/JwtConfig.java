package cust.aowei.jwtstudy.config;

import cust.aowei.jwtstudy.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author aowei
 */
@Configuration
public class JwtConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器的配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        // 添加自定义拦截器
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**");
    }
    /**
     * 跨域支持
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS", "HEAD")
                .maxAge(3600 * 24);
    }
}
