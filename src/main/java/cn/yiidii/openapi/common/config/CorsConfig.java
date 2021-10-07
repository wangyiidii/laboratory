package cn.yiidii.openapi.common.config;

import cn.yiidii.openapi.free.common.interceptor.DeprecatedApiInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 *
 * @author YiiDii Wang
 * @date 2021/6/3 22:24:45
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final DeprecatedApiInterceptor deprecatedApiInterceptor;

    /**
     * 开启跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路由
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                //.allowedOriginPatterns("*")
                .allowedOrigins("*")
                // 是否允许证书（cookies）
                .allowCredentials(true)
                // 设置允许的方法
                .allowedMethods("*")
                // 跨域允许时间
                .maxAge(3600);
    }

    /**
     * 拦截器
     *
     * @param registry registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deprecatedApiInterceptor);
    }
}