package cn.yiidii.openapi.common.config;

import cn.yiidii.pigeon.common.core.constant.SecurityConstant;
import cn.yiidii.pigeon.common.security.service.PigeonUser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * 认证服务器需要的bean
 *
 * @author YiiDii Wang
 * @create 2021-08-21 16:28
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizationServerBeanConfig {

    private final DataSource dataSource;
    private final TokenStore tokenStore;
    private final JwtAccessTokenConverter accessTokenConverter;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 配置授权码服务（授权码模式会用到）
     *
     * @return AuthorizationCodeServices
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
        return new JdbcAuthorizationCodeServices(dataSource);
    }


    /**
     * 客户端详情服务Bean
     */
    @Bean
    public ClientDetailsService clientDetails() {
        final JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(passwordEncoder());
        return jdbcClientDetailsService;
    }

    /**
     * 配置令牌的管理
     */
    @Bean
    public AuthorizationServerTokenServices authorizationServerTokenServices() {
        DefaultTokenServices service = new DefaultTokenServices();
        // 客户端详情服务
        service.setClientDetailsService(clientDetails());
        // 支持刷新令牌
        service.setSupportRefreshToken(true);
        // 令牌存储策略
        service.setTokenStore(tokenStore);
        // 令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Lists.newArrayList(tokenEnhancer(), accessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);
        // 令牌默认有效期2小时
        service.setAccessTokenValiditySeconds(7200);
        // 刷新令牌默认有效期3天
        service.setRefreshTokenValiditySeconds(259200);
        return service;
    }

    /**
     * jwt token增强，添加额外信息
     *
     * @return TokenEnhancer
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (oAuth2AccessToken, oAuth2Authentication) -> {
            // 添加额外信息的map
            final Map<String, Object> additionMessage = Maps.newHashMap();
            // 对于客户端鉴权模式，直接返回token
            if (oAuth2Authentication.getUserAuthentication() == null) {
                return oAuth2AccessToken;
            }
            // 获取当前登录的用户
            PigeonUser user = (PigeonUser) oAuth2Authentication.getUserAuthentication().getPrincipal();
            // 额外信息
            if (user != null) {
                additionMessage.put(SecurityConstant.USER_ID, String.valueOf(user.getId()));
                additionMessage.put(SecurityConstant.USERNAME, user.getUsername());
            }
            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionMessage);
            return oAuth2AccessToken;
        };
    }

}
