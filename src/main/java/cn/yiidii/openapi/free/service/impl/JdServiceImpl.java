package cn.yiidii.openapi.free.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yiidii.openapi.common.constant.RedisKeyConstant;
import cn.yiidii.openapi.free.model.dto.jd.JdInfo;
import cn.yiidii.openapi.free.service.IJdService;
import cn.yiidii.openapi.free.service.exception.jd.JdException;
import cn.yiidii.pigeon.common.redis.core.RedisOps;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author YiiDii Wang
 * @create 2021-06-01 10:17
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class JdServiceImpl implements IJdService {

    private static final String APP_ID = "959";
    private static final String Q_VERSION = "1.0.0";
    private static final String COUNTRY_CODE = "86";

    private final RedisOps redisOps;


    @Override
    public JdInfo sendSmsCode(String mobile) throws Exception {
        String subCmd = "1";
        long timestamp = System.currentTimeMillis();

        // 第一步，获取一堆什么参数
        String sign = DigestUtil.md5Hex(StrUtil.format("{}{}{}36{}sb2cwlYyaCSN1KUv5RHG3tmqxfEb8NKN", APP_ID, Q_VERSION, timestamp, subCmd));
        String param = StrUtil.format("client_ver=1.0.0&gsign={}&appid={}&return_page=https%3A%2F%2Fcrpl.jd.com%2Fn%2Fmine%3FpartnerId%3DWBTF0KYY%26ADTAG%3Dkyy_mrqd%26token%3D&cmd=36&sdk_ver=1.0.0&sub_cmd=1&qversion={}&ts={}", sign, APP_ID, Q_VERSION, timestamp);
        HttpResponse response = HttpRequest.post("https://qapplogin.m.jd.com/cgi-bin/qapp/quick")
                .body(param, ContentType.FORM_URLENCODED.toString())
                .execute();
        JSONObject responseJo = JSONObject.parseObject(response.body());
        this.checkErr(responseJo);
        JSONObject data = responseJo.getJSONObject("data");
        String gsalt = data.getString("gsalt");
        String guid = data.getString("guid");
        String lsid = data.getString("lsid");
        String rsaModulus = data.getString("rsa_modulus");
        String ck = StrUtil.format("guid={}; lsid={}; gsalt={};rsa_modulus={};", guid, lsid, gsalt, rsaModulus);
        JdInfo jdInfo = JdInfo.builder()
                .gsalt(gsalt)
                .guid(guid)
                .lsId(lsid)
                .gsalt(gsalt)
                .rsaModulus(rsaModulus)
                .preCookie(ck)
                .build();

        // 第二步，发送验证码
        timestamp = System.currentTimeMillis();
        subCmd = "2";
        String gsign = DigestUtil.md5Hex(StrUtil.format("{}{}{}36{}{}", APP_ID, Q_VERSION, timestamp, subCmd, gsalt));
        sign = DigestUtil.md5Hex(StrUtil.format("{}{}{}{}4dtyyzKF3w6o54fJZnmeW3bVHl0$PbXj", APP_ID, Q_VERSION, COUNTRY_CODE, mobile));
        param = StrUtil.format("country_code={}&client_ver=1.0.0&gsign={}&appid={}&mobile={}&sign={}&cmd=36&sub_cmd={}&qversion={}&ts={}", COUNTRY_CODE, gsign, APP_ID, mobile, sign, subCmd, Q_VERSION, timestamp);

        response = HttpRequest.post("https://qapplogin.m.jd.com/cgi-bin/qapp/quick")
                .body(param, ContentType.FORM_URLENCODED.toString())
                .cookie(ck)
                .execute();
        responseJo = JSONObject.parseObject(response.body());
        this.checkErr(responseJo);
        redisOps.set(StrUtil.format(RedisKeyConstant.JD_LOGIN_TEMP_INFO, mobile),
                JSONObject.toJSONString(jdInfo),
                RedisKeyConstant.JD_LOGIN_TEMP_INFO_EXPIRE);
        return jdInfo;
    }

    @Override
    public JdInfo login(String mobile, String code) throws Exception {
        String key = StrUtil.format(RedisKeyConstant.JD_LOGIN_TEMP_INFO, mobile);
        Object o = redisOps.get(key);
        if (Objects.isNull(o)) {
            throw new JdException(-1, "请先获取验证码");
        }
        JdInfo jdInfo = JSONObject.parseObject(JSONObject.parseObject(o.toString()).toJSONString(), JdInfo.class);

        String subCmd = "3";
        long timestamp = System.currentTimeMillis();
        String gsign = StrUtil.format("{}{}{}36{}{}", APP_ID, Q_VERSION, timestamp, subCmd, jdInfo.getGsalt());
        String param = StrUtil.format("country_code={}&client_ver=1.0.0&gsign={}&smscode={}&appid={}&mobile={}&cmd=36&sub_cmd={}&qversion={}&ts={}", COUNTRY_CODE, gsign, code, APP_ID, mobile, subCmd, Q_VERSION, timestamp);
        HttpResponse response = HttpRequest.post("https://qapplogin.m.jd.com/cgi-bin/qapp/quick")
                .body(param, ContentType.FORM_URLENCODED.toString())
                .cookie(jdInfo.getPreCookie())
                .execute();
        JSONObject responseJo = JSONObject.parseObject(response.body());
        this.checkErr(responseJo);
        JSONObject data = responseJo.getJSONObject("data");
        String ptKey = data.getString("pt_key");
        String ptPin = data.getString("pt_pin");
        String cookie = StrUtil.format("pt_key={}; pt_pin={};", ptKey, ptPin);
        redisOps.del(key);
        return new JdInfo().builder().cookie(cookie).build();
    }

    @Override
    public JdInfo getByWsKey(String key) throws Exception {
        // genToken的body参数
        JSONObject jo = new JSONObject();
        jo.put("action", "to");
        jo.put("to", cn.hutool.core.net.URLEncoder.ALL.encode("https://plogin.m.jd.com/cgi-bin/m/thirdapp_auth_page?token=AAEAIEijIw6wxF2s3bNKF0bmGsI8xfw6hkQT6Ui2QVP7z1Xg&client_type=android&appid=879&appup_type=1", Charset.forName(CharsetUtil.UTF_8)));
        // 请求genToken接口获取
        String body = HttpRequest.post("https://api.m.jd.com:443/client.action?functionId=genToken&clientVersion=10.1.2&client=android&lang=zh_CN&uuid=09d53a5653402b1f&st=1630392618706&sign=53904736db53eebc01ca70036e7187d6&sv=120")
                .header(Header.COOKIE, key)
                .body(StrUtil.format("body={}", cn.hutool.core.net.URLEncoder.ALL.encode(jo.toJSONString(), Charset.forName(CharsetUtil.UTF_8))), ContentType.FORM_URLENCODED.getValue())
                .execute().body();
        JSONObject bodyJo = JSONObject.parseObject(body);

        // 跳转参数
        Map<String, Object> prams = Maps.newHashMap();
        prams.put("tokenKey", bodyJo.getString("tokenKey"));
        prams.put("to", "https://plogin.m.jd.com/cgi-bin/m/thirdapp_auth_page?token=AAEAIEijIw6wxF2s3bNKF0bmGsI8xfw6hkQT6Ui2QVP7z1Xg");
        prams.put("client_type", "android");
        prams.put("appid", 879);
        prams.put("appup_type", 1);
        String paramStr = CollUtil.join(prams.entrySet().stream().map(e -> StrUtil.format("{}={}", e.getKey(), e.getValue())).collect(Collectors.toList()), "&");
        // 302重定向
        String jmpUrl = StrUtil.format("{}?{}", bodyJo.getString("url"), paramStr);
        HttpResponse response = HttpRequest.get(jmpUrl).execute();
        String ptKey = response.getCookie("pt_key").getValue();
        String ptPin = response.getCookie("pt_pin").getValue();
        if (StrUtil.contains(ptKey, "fake_") || StrUtil.contains(ptPin, "***")) {
            throw new JdException(-1, "非法faker用户");
        }
        return JdInfo.builder()
                .preCookie(response.getCookieStr())
                .cookie(StrUtil.format("pt_key={}; pt_pin={}", ptKey, ptPin))
                .build();
    }


    private Map<String, String> transSetCookie2Map(List<String> setCookiesList) {
        if (CollectionUtils.isEmpty(setCookiesList)) {
            return Maps.newHashMap();
        }
        return setCookiesList.stream()
                .map(item -> item.split(";"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(s -> {
                    final String[] split = s.split("=");
                    return split.length > 1 && StrUtil.isNotBlank(split[1]);
                })
                .distinct()
                .collect(Collectors.toMap(
                        s -> s.split("=")[0],
                        s -> s.split("=")[1],
                        (s1, s2) -> s2
                ));
    }

    private void checkErr(JSONObject responseJo) {
        Integer errCode = responseJo.getInteger("err_code");
        if (errCode != 0) {
            throw new JdException(-1, responseJo.getString("err_msg"));
        }
    }
}
