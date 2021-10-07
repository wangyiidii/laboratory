package cn.yiidii.openapi.uaa.config;

import cn.yiidii.pigeon.common.security.config.IgnoreUrlProperties;
import cn.yiidii.pigeon.common.security.service.PigeonUserDetailsService;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 安全配置
 *
 * @author YiiDii Wang
 * @create 2021-10-02 08:04
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final IgnoreUrlProperties ignoreUrlProperties;
    private final PigeonUserDetailsService pigeonUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<String> ignoreUrls = ignoreUrlProperties.getIgnoreUrls();
        log.info("ignoreUrls: {}", ignoreUrls);
        http
                .formLogin()
                .and().authorizeRequests().requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .and().authorizeRequests()
                .antMatchers(ignoreUrls.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()
                .cors().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(pigeonUserDetailsService);
    }

    /**
     * 认证管理器 （密码模式需要, grant_type=password）
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
