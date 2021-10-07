package cn.yiidii.openapi.uaa.config;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * @author YiiDii Wang
 * @create 2021-10-02 17:58
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final AuthorizationCodeServices authorizationCodeServices;
    private final AuthenticationManager authenticationManager;
    private final ClientDetailsService clientDetails;
    private final AuthorizationServerTokenServices authorizationServerTokenServices;

    /**
     * 客户端详情服务配置
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails);
    }

    /**
     * 令牌访问端点的安全配置
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //参数：permitAll() 公开；对应 oauth/token_key 的端点（提供公有密钥的接口url,如果你使用jwt令牌的话）
                .tokenKeyAccess("permitAll()")
                //参数：permitAll() 公开；对应 oauth/token_key 的端点 (资源服务访问的令牌解析校验接口的url)
                .checkTokenAccess("permitAll()")
                //允许表单认证，申请令牌
                .allowFormAuthenticationForClients();
    }

    /**
     * 配置令牌访问端点
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                // 密码模式需要
                .authenticationManager(authenticationManager)
                // 授权码需要
                .authorizationCodeServices(authorizationCodeServices)
                // 自己配置的令牌管理
                .tokenServices(authorizationServerTokenServices)
                // 访问端点映射
                // .pathMapping("/oauth/confirm_access", "/custom/confirm_access")
                // 允许get，post请求获取 token,及访问端点 oauth/token
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                // 支持的granter
                .tokenGranter(getSupportedGranters(endpoints));
    }

    /**
     * 配置支持的gtanter
     *
     * @param endpoints endpoints
     * @return CompositeTokenGranter
     */
    private CompositeTokenGranter getSupportedGranters(AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> list = new ArrayList<>();
        // 授权码模式
        list.add(new AuthorizationCodeTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getAuthorizationCodeServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory()));
        // 简化模式
        list.add(new ImplicitTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory()));

        // 客户端模式
        list.add(new ClientCredentialsTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));

        // 刷新token模式
        list.add(new RefreshTokenGranter
                (endpoints.getTokenServices(),
                        endpoints.getClientDetailsService(),
                        endpoints.getOAuth2RequestFactory()));

        // 密码模式 和 社交登录模式
        if (authenticationManager != null) {
            list.add(new ResourceOwnerPasswordTokenGranter(authenticationManager,
                    endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(),
                    endpoints.getOAuth2RequestFactory()));
        }

        return new CompositeTokenGranter(list);
    }
}
