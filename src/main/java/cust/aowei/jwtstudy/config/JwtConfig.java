package cust.aowei.jwtstudy.config;

import cust.aowei.jwtstudy.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author aowei
 */
@Configuration
public class JwtConfig extends WebMvcConfigurationSupport {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 添加拦截器的配置
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry){
        // 添加自定义拦截器
        registry.addInterceptor(jwtInterceptor)
                // 指定拦截器的url
                .addPathPatterns("/**")
                // 指定不拦截的url
                .excludePathPatterns("/login","/test","/register/**");
    }
}
