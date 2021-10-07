package cn.yiidii.openapi.uaa.controller;

import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.common.core.constant.SecurityConstant;
import cn.yiidii.pigeon.common.core.exception.BizException;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * OauthController
 *
 * @author YiiDii Wang
 * @create 2021-10-02 17:52
 */
@RestController
@RequestMapping("/oauth")
@AllArgsConstructor
@Api(tags = "Oauth2管理")
public class OauthController {

    private final TokenEndpoint tokenEndpoint;

    @GetMapping("/token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grant_type", value = "授权类型", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "code", value = "授权码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "redirect_uri", value = "回调地址", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query", dataType = "String")
    })
    @ApiOperation(value = "用户登录Get", notes = "用户登录Get")
    public R<?> getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oauth2AccessToken = tokenEndpoint.getAccessToken(principal, parameters).getBody();
        Assert.isTrue(Objects.nonNull(oauth2AccessToken), "登陆失败");
        return R.ok(this.wrapToken(oauth2AccessToken));
    }

    /**
     * 重写token接口
     *
     * @param principal principal
     * @param params    params
     * @return R
     * @throws HttpRequestMethodNotSupportedException e
     */
    @PostMapping("/token")
    @ApiOperation("获取token接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grant_type", value = "授权类型", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "code", value = "授权码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "redirect_uri", value = "回调地址", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query", dataType = "String")
    })
    public R<?> postAccessToken(Principal principal, @RequestBody Map<String, String> params) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(principal, params).getBody();
        if (Objects.isNull(accessToken)) {
            throw new BizException("登陆失败");
        }
        return R.ok(this.wrapToken(accessToken));
    }


    private JSONObject wrapToken(OAuth2AccessToken oAuth2AccessToken) {
        final JSONObject jo = new JSONObject();
        jo.put(SecurityConstant.ACCESS_TOKEN, oAuth2AccessToken.getValue());
        final OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
        if (Objects.nonNull(refreshToken)) {
            jo.put(SecurityConstant.REFRESH_TOKEN, oAuth2AccessToken.getRefreshToken().getValue());
        }
        jo.put(SecurityConstant.TOKEN_TYPE, oAuth2AccessToken.getTokenType());
        Map<String, Object> additionalInfo = oAuth2AccessToken.getAdditionalInformation();
        additionalInfo.remove("jti");
        jo.putAll(additionalInfo);
        return jo;
    }
}
