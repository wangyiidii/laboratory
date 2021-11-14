package cn.yiidii.openapi.free.component.signin;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yiidii.openapi.common.util.AdminNotifyUtil;
import cn.yiidii.openapi.free.model.form.signin.MiBrushStepForm;
import cn.yiidii.openapi.free.model.vo.mail.AdminNotifyVO;
import cn.yiidii.pigeon.common.core.exception.BizException;
import cn.yiidii.pigeon.common.redis.core.RedisOps;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 刷步数
 *
 * @author YiiDii Wang
 * @create 2021-10-10 21:54
 */
@Slf4j
@Component("miBrushStepComponent")
@RequiredArgsConstructor
public class MiBrushStepComponent {

    public static final String KEY_MI_BRUSH_STEP_INFO = "miBrushStepInfo";

    private final RedisOps redisOps;
    private final AdminNotifyUtil adminNotifyUtil;

    /**
     * 刷新步数
     *
     * @param form     参数
     * @param isNotify 是否通知（为了定时任务不通知）
     */
    public void brushStep(MiBrushStepForm form, boolean isNotify) {
        String phone = form.getPhone();
        String password = form.getPassword();
        Assert.isTrue(Objects.nonNull(phone), "手机号不能为空");
        Assert.isTrue(Objects.nonNull(password), "密码不能为空");
        Long step = form.getStep();
        if (step < 0) {
            step = 18888L;
        }

        // 获取access
        Map<String, Object> params = Maps.newHashMap();
        params.put("client_id", "HuaMi");
        params.put("password", password);
        params.put("redirect_uri", "https://s3-us-west-2.amazonaws.com/hm-registration/successsignin.html");
        params.put("token", "access");
        HttpResponse response = HttpRequest.post(StrUtil.format("https://api-user.huami.com/registrations/+86{}/tokens", phone))
                .form(params)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8")
                .header(Header.USER_AGENT, "MiFit/4.6.0 (iPhone; iOS 14.0.1; Scale/2.00)")
                .execute();
        String location = response.header(Header.LOCATION);
        String access = ReUtil.get("(?<=access=).*?(?=&)", location, 0);
        if (StrUtil.isBlank(access)) {
            throw new BizException("获取授权码失败(可能密码错误)");
        }

        // 获取loginToken和uerId
        params.clear();
        params.put("app_version", "4.6.0");
        params.put("code", access);
        params.put("country_code", "CN");
        params.put("device_id", "2C8B4939-0CCD-4E94-8CBA-CB8EA6E613A1");
        params.put("device_model", "phone");
        params.put("grant_type", "access_token");
        params.put("third_name", "huami_phone");
        params.put("app_name", "com.xiaomi.hm.health");
        response = HttpRequest.post("https://account.huami.com/v2/client/login")
                .form(params)
                .header(Header.USER_AGENT, "MiFit/4.6.0 (iPhone; iOS 14.0.1; Scale/2.00)")
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8")
                .execute();
        JSONObject accessTokenResp = JSONObject.parseObject(response.body());
        String loginToken = accessTokenResp.getJSONObject("token_info").getString("login_token");
        String userId = accessTokenResp.getJSONObject("token_info").getString("user_id");

        // 获取appToken
        response = HttpRequest.get(StrUtil.format("https://account-cn.huami.com/v1/client/app_tokens?app_name=com.xiaomi.hm.health&dn=api-user.huami.com%2Capi-mifit.huami.com%2Capp-analytics.huami.com&login_token={}&os_version=4.1.0", loginToken))
                .execute();
        JSONObject appTokenResp = JSONObject.parseObject(response.body());
        String appToken = appTokenResp.getJSONObject("token_info").getString("app_token");

        // 打卡
        String dateStr = DateUtil.formatDate(new Date());
        String dataJson =
                "[{\"data_hr\":\"\\/\\/\\/\\/\\/\\/9L\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/Vv\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/0v\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/9e\\/\\/\\/\\/\\/0n\\/a\\/\\/\\/S\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/0b\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/1FK\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/R\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/9PTFFpaf9L\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/R\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/0j\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/9K\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/Ov\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/zf\\/\\/\\/86\\/zr\\/Ov88\\/zf\\/Pf\\/\\/\\/0v\\/S\\/8\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/Sf\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/z3\\/\\/\\/\\/\\/\\/0r\\/Ov\\/\\/\\/\\/\\/\\/S\\/9L\\/zb\\/Sf9K\\/0v\\/Rf9H\\/zj\\/Sf9K\\/0\\/\\/N\\/\\/\\/\\/0D\\/Sf83\\/zr\\/Pf9M\\/0v\\/Ov9e\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/S\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/zv\\/\\/z7\\/O\\/83\\/zv\\/N\\/83\\/zr\\/N\\/86\\/z\\/\\/Nv83\\/zn\\/Xv84\\/zr\\/PP84\\/zj\\/N\\/9e\\/zr\\/N\\/89\\/03\\/P\\/89\\/z3\\/Q\\/9N\\/0v\\/Tv9C\\/0H\\/Of9D\\/zz\\/Of88\\/z\\/\\/PP9A\\/zr\\/N\\/86\\/zz\\/Nv87\\/0D\\/Ov84\\/0v\\/O\\/84\\/zf\\/MP83\\/zH\\/Nv83\\/zf\\/N\\/84\\/zf\\/Of82\\/zf\\/OP83\\/zb\\/Mv81\\/zX\\/R\\/9L\\/0v\\/O\\/9I\\/0T\\/S\\/9A\\/zn\\/Pf89\\/zn\\/Nf9K\\/07\\/N\\/83\\/zn\\/Nv83\\/zv\\/O\\/9A\\/0H\\/Of8\\/\\/zj\\/PP83\\/zj\\/S\\/87\\/zj\\/Nv84\\/zf\\/Of83\\/zf\\/Of83\\/zb\\/Nv9L\\/zj\\/Nv82\\/zb\\/N\\/85\\/zf\\/N\\/9J\\/zf\\/Nv83\\/zj\\/Nv84\\/0r\\/Sv83\\/zf\\/MP\\/\\/\\/zb\\/Mv82\\/zb\\/Of85\\/z7\\/Nv8\\/\\/0r\\/S\\/85\\/0H\\/QP9B\\/0D\\/Nf89\\/zj\\/Ov83\\/zv\\/Nv8\\/\\/0f\\/Sv9O\\/0ZeXv\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/1X\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/9B\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/TP\\/\\/\\/1b\\/\\/\\/\\/\\/\\/0\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/9N\\/\\/\\/\\/\\/\\/\\/\\/\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\\/v7+\",\"date\":\""
                        + dateStr
                        + "\",\"data\":[{\"start\":0,\"stop\":1439,\"value\":\"UA8AUBQAUAwAUBoAUAEAYCcAUBkAUB4AUBgAUCAAUAEAUBkAUAwAYAsAYB8AYB0AYBgAYCoAYBgAYB4AUCcAUBsAUB8AUBwAUBIAYBkAYB8AUBoAUBMAUCEAUCIAYBYAUBwAUCAAUBgAUCAAUBcAYBsAYCUAATIPYD0KECQAYDMAYB0AYAsAYCAAYDwAYCIAYB0AYBcAYCQAYB0AYBAAYCMAYAoAYCIAYCEAYCYAYBsAYBUAYAYAYCIAYCMAUB0AUCAAUBYAUCoAUBEAUC8AUB0AUBYAUDMAUDoAUBkAUC0AUBQAUBwAUA0AUBsAUAoAUCEAUBYAUAwAUB4AUAwAUCcAUCYAUCwKYDUAAUUlEC8IYEMAYEgAYDoAYBAAUAMAUBkAWgAAWgAAWgAAWgAAWgAAUAgAWgAAUBAAUAQAUA4AUA8AUAkAUAIAUAYAUAcAUAIAWgAAUAQAUAkAUAEAUBkAUCUAWgAAUAYAUBEAWgAAUBYAWgAAUAYAWgAAWgAAWgAAWgAAUBcAUAcAWgAAUBUAUAoAUAIAWgAAUAQAUAYAUCgAWgAAUAgAWgAAWgAAUAwAWwAAXCMAUBQAWwAAUAIAWgAAWgAAWgAAWgAAWgAAWgAAWgAAWgAAWREAWQIAUAMAWSEAUDoAUDIAUB8AUCEAUC4AXB4AUA4AWgAAUBIAUA8AUBAAUCUAUCIAUAMAUAEAUAsAUAMAUCwAUBYAWgAAWgAAWgAAWgAAWgAAWgAAUAYAWgAAWgAAWgAAUAYAWwAAWgAAUAYAXAQAUAMAUBsAUBcAUCAAWwAAWgAAWgAAWgAAWgAAUBgAUB4AWgAAUAcAUAwAWQIAWQkAUAEAUAIAWgAAUAoAWgAAUAYAUB0AWgAAWgAAUAkAWgAAWSwAUBIAWgAAUC4AWSYAWgAAUAYAUAoAUAkAUAIAUAcAWgAAUAEAUBEAUBgAUBcAWRYAUA0AWSgAUB4AUDQAUBoAXA4AUA8AUBwAUA8AUA4AUA4AWgAAUAIAUCMAWgAAUCwAUBgAUAYAUAAAUAAAUAAAUAAAUAAAUAAAUAAAUAAAUAAAWwAAUAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAeSEAeQ8AcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcBcAcAAAcAAAcCYOcBUAUAAAUAAAUAAAUAAAUAUAUAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcCgAeQAAcAAAcAAAcAAAcAAAcAAAcAYAcAAAcBgAeQAAcAAAcAAAegAAegAAcAAAcAcAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcCkAeQAAcAcAcAAAcAAAcAwAcAAAcAAAcAIAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcCIAeQAAcAAAcAAAcAAAcAAAcAAAeRwAeQAAWgAAUAAAUAAAUAAAUAAAUAAAcAAAcAAAcBoAeScAeQAAegAAcBkAeQAAUAAAUAAAUAAAUAAAUAAAUAAAcAAAcAAAcAAAcAAAcAAAcAAAegAAegAAcAAAcAAAcBgAeQAAcAAAcAAAcAAAcAAAcAAAcAkAegAAegAAcAcAcAAAcAcAcAAAcAAAcAAAcAAAcA8AeQAAcAAAcAAAeRQAcAwAUAAAUAAAUAAAUAAAUAAAUAAAcAAAcBEAcA0AcAAAWQsAUAAAUAAAUAAAUAAAUAAAcAAAcAoAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAYAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcBYAegAAcAAAcAAAegAAcAcAcAAAcAAAcAAAcAAAcAAAeRkAegAAegAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAEAcAAAcAAAcAAAcAUAcAQAcAAAcBIAeQAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcBsAcAAAcAAAcBcAeQAAUAAAUAAAUAAAUAAAUAAAUBQAcBYAUAAAUAAAUAoAWRYAWTQAWQAAUAAAUAAAUAAAcAAAcAAAcAAAcAAAcAAAcAMAcAAAcAQAcAAAcAAAcAAAcDMAeSIAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcAAAcBQAeQwAcAAAcAAAcAAAcAMAcAAAeSoAcA8AcDMAcAYAeQoAcAwAcFQAcEMAeVIAaTYAbBcNYAsAYBIAYAIAYAIAYBUAYCwAYBMAYDYAYCkAYDcAUCoAUCcAUAUAUBAAWgAAYBoAYBcAYCgAUAMAUAYAUBYAUA4AUBgAUAgAUAgAUAsAUAsAUA4AUAMAUAYAUAQAUBIAASsSUDAAUDAAUBAAYAYAUBAAUAUAUCAAUBoAUCAAUBAAUAoAYAIAUAQAUAgAUCcAUAsAUCIAUCUAUAoAUA4AUB8AUBkAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAAfgAA\",\"tz\":32,\"did\":\"DA932FFFFE8816E7\",\"src\":24}],\"summary\":\"{\\\"v\\\":6,\\\"slp\\\":{\\\"st\\\":1628296479,\\\"ed\\\":1628296479,\\\"dp\\\":0,\\\"lt\\\":0,\\\"wk\\\":0,\\\"usrSt\\\":-1440,\\\"usrEd\\\":-1440,\\\"wc\\\":0,\\\"is\\\":0,\\\"lb\\\":0,\\\"to\\\":0,\\\"dt\\\":0,\\\"rhr\\\":0,\\\"ss\\\":0},\\\"stp\\\":{\\\"ttl\\\":"
                        + step
                        + ",\\\"dis\\\":10627,\\\"cal\\\":510,\\\"wk\\\":41,\\\"rn\\\":50,\\\"runDist\\\":7654,\\\"runCal\\\":397,\\\"stage\\\":[{\\\"start\\\":327,\\\"stop\\\":341,\\\"mode\\\":1,\\\"dis\\\":481,\\\"cal\\\":13,\\\"step\\\":680},{\\\"start\\\":342,\\\"stop\\\":367,\\\"mode\\\":3,\\\"dis\\\":2295,\\\"cal\\\":95,\\\"step\\\":2874},{\\\"start\\\":368,\\\"stop\\\":377,\\\"mode\\\":4,\\\"dis\\\":1592,\\\"cal\\\":88,\\\"step\\\":1664},{\\\"start\\\":378,\\\"stop\\\":386,\\\"mode\\\":3,\\\"dis\\\":1072,\\\"cal\\\":51,\\\"step\\\":1245},{\\\"start\\\":387,\\\"stop\\\":393,\\\"mode\\\":4,\\\"dis\\\":1036,\\\"cal\\\":57,\\\"step\\\":1124},{\\\"start\\\":394,\\\"stop\\\":398,\\\"mode\\\":3,\\\"dis\\\":488,\\\"cal\\\":19,\\\"step\\\":607},{\\\"start\\\":399,\\\"stop\\\":414,\\\"mode\\\":4,\\\"dis\\\":2220,\\\"cal\\\":120,\\\"step\\\":2371},{\\\"start\\\":415,\\\"stop\\\":427,\\\"mode\\\":3,\\\"dis\\\":1268,\\\"cal\\\":59,\\\"step\\\":1489},{\\\"start\\\":428,\\\"stop\\\":433,\\\"mode\\\":1,\\\"dis\\\":152,\\\"cal\\\":4,\\\"step\\\":238},{\\\"start\\\":434,\\\"stop\\\":444,\\\"mode\\\":3,\\\"dis\\\":2295,\\\"cal\\\":95,\\\"step\\\":2874},{\\\"start\\\":445,\\\"stop\\\":455,\\\"mode\\\":4,\\\"dis\\\":1592,\\\"cal\\\":88,\\\"step\\\":1664},{\\\"start\\\":456,\\\"stop\\\":466,\\\"mode\\\":3,\\\"dis\\\":1072,\\\"cal\\\":51,\\\"step\\\":1245},{\\\"start\\\":467,\\\"stop\\\":477,\\\"mode\\\":4,\\\"dis\\\":1036,\\\"cal\\\":57,\\\"step\\\":1124},{\\\"start\\\":478,\\\"stop\\\":488,\\\"mode\\\":3,\\\"dis\\\":488,\\\"cal\\\":19,\\\"step\\\":607},{\\\"start\\\":489,\\\"stop\\\":499,\\\"mode\\\":4,\\\"dis\\\":2220,\\\"cal\\\":120,\\\"step\\\":2371},{\\\"start\\\":500,\\\"stop\\\":511,\\\"mode\\\":3,\\\"dis\\\":1268,\\\"cal\\\":59,\\\"step\\\":1489},{\\\"start\\\":512,\\\"stop\\\":522,\\\"mode\\\":1,\\\"dis\\\":152,\\\"cal\\\":4,\\\"step\\\":238}]},\\\"goal\\\":8000,\\\"tz\\\":\\\"28800\\\"}\",\"source\":24,\"type\":0}]";
        params.clear();
        params.put("data_json", dataJson);
        params.put("userid", userId);
        params.put("device_type", "0");
        params.put("last_sync_data_time", DateUtil.offset(new Date(), DateField.HOUR, -1).toJdkDate().getTime() / 1000);
        params.put("last_deviceid", "DA932FFFFE8816E7");
        response = HttpRequest.post(StrUtil.format("https://api-mifit-cn.huami.com/v1/data/band_data.json?&t={}", System.currentTimeMillis()))
                .form(params)
                .header("apptoken", appToken)
                .execute();
        JSONObject bandDataResp = JSONObject.parseObject(response.body());
        boolean success = bandDataResp.getInteger("code") == 1 || StrUtil.equals(bandDataResp.getString("message"), "success");
        if (!success) {
            throw new BizException(bandDataResp.getString("message"));
        }

        // isAuto
        if (form.isAuto()) {
            // 自动执行, 放进redis
            redisOps.hset(KEY_MI_BRUSH_STEP_INFO, phone, JSONObject.toJSONString(form));
        } else {
            // 非自动执行, 从redis移除
            redisOps.hdel(KEY_MI_BRUSH_STEP_INFO, phone);
        }

        // 通知
        if (isNotify) {
            String content = StrUtil.format("{}刷新了{}步", DesensitizedUtil.mobilePhone(phone), step);
            adminNotifyUtil.doNotify(content, new AdminNotifyVO().setContent(content));
        }
    }

}
