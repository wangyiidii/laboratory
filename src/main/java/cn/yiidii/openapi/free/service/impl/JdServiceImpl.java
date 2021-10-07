package cn.yiidii.openapi.free.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yiidii.openapi.free.model.dto.jd.JdInfo;
import cn.yiidii.openapi.free.service.IJdService;
import cn.yiidii.openapi.free.service.exception.jd.JdException;
import cn.yiidii.pigeon.common.core.exception.BizException;
import cn.yiidii.pigeon.common.core.util.HttpClientUtil;
import cn.yiidii.pigeon.common.core.util.dto.HttpClientResult;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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

    private final Environment environment;

    /**
     * 获取京东信息
     *
     * @return JdInfo
     */
    @Override
    public JdInfo getQrCode() throws Exception {
        JdInfo jdInfo = this.loginEntranceWithHttpClientUtil();
        this.getQrCodeUrl(jdInfo);
        String base64 = this.trans2ImgBase64(jdInfo.getQrCodeUrl());
        jdInfo.setQrCodeBase64(base64);
        return jdInfo;
    }

    @Override
    public JdInfo checkLogin(JdInfo info) throws Exception {
        long currMs = System.currentTimeMillis();
        String cookieUrl = "https://plogin.m.jd.com/cgi-bin/m/tmauthchecktoken?&token=" + info.getToken() + "&ou_state=0&okl_token=" + info.getOklToken();
        HttpResponse resp = HttpRequest.post(cookieUrl)
                .form("lang", "chs")
                .form("appid", 300)
                .form("returnurl", URLEncoder
                        .encode("https://wqlogin2.jd.com/passport/LoginRedirect?state=1100399130787&returnurl=//home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&/myJd/home.action", "utf-8"))
                .form("source", "wq_passport")
                .header("Referer", "https://plogin.m.jd.com/login/login?appid=300&returnurl=https://wqlogin2.jd.com/passport/LoginRedirect?state=" + currMs
                        + "&returnurl=//home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&/myJd/home.action&source=wq_passport")
                .header("Cookie", info.getPreCookie())
                .header("User-Agent", info.getUa())
                .execute();
        JSONObject respJo = JSONObject.parseObject(resp.body());
        int errCode = respJo.getInteger("errcode");
        if (errCode != 0) {
            String message = respJo.getString("message");
            throw new JdException(errCode, message);
        }
        Map<String, String> setCookieKv = transSetCookie2Map(resp.headerList("Set-Cookie"));
        String ptKey = setCookieKv.get("pt_key");
        String ptPin = setCookieKv.get("pt_pin");
        String cookie = StrUtil.format("pt_key={}; pt_pin={};", ptKey, ptPin);
        info.setCookie(cookie);
        return info;
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

    /**
     * loginEntrance, 获取一些必要的token信息
     * <p>
     * 不明原因，hutool的获取，lsid有时候为空，改用apache httpclient的
     * </p>
     *
     * @return JdInfo
     * @throws Exception e
     */
    private JdInfo loginEntranceWithHttpClientUtil() throws Exception {
        String ua = environment.getProperty("pigeon.jd.ua");
        ua = StrUtil.isNotBlank(ua) ? ua : randomUa();
        long currMs = System.currentTimeMillis();
        String loginEntranceUrl = "https://plogin.m.jd.com/cgi-bin/mm/new_login_entrance?lang=chs&appid=300&returnurl=https://wq.jd.com/passport/LoginRedirect?state=" + currMs
                + "&returnurl=https://home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&/myJd/home.action&source=wq_passport&_t=" + currMs;
        final HashMap<String, String> param = Maps.newHashMap();
        final HashMap<String, String> header = Maps.newHashMap();
        header.put("Connection", "Keep-Alive");
        header.put("Content-Type", ContentType.FORM_URLENCODED.getValue());
        header.put("Accept", "application/json, text/plain, */*");
        header.put("Accept-Language", "zh-cn");
        header.put("Referer", URLEncoder.encode("https://plogin.m.jd.com/login/login?appid=300&returnurl=https://wq.jd.com/passport/LoginRedirect?state=" + currMs
                + "&returnurl=https://home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&/myJd/home.action&source=wq_passport", "utf-8"));
        header.put("User-Agent", ua);
        header.put("Host", "plogin.m.jd.com");
        final HttpClientResult httpClientResult = HttpClientUtil.doGet(loginEntranceUrl, param, header);
        final String cookieStr = httpClientResult.getCookieStr();
        if (httpClientResult.getCode() != 200 || StrUtil.isBlank(cookieStr)) {
            throw new BizException("aaa");
        }
        String sToken = JSONObject.parseObject(httpClientResult.getContent()).getString("s_token");
        Map<String, String> setCookieKv = transSetCookie2Map(Arrays.stream(cookieStr.split(";")).distinct().collect(Collectors.toList()));
        String gUid = setCookieKv.get("guid");
        String lsId = setCookieKv.get("lsid");
        String lsToken = setCookieKv.get("lstoken");
        if (StrUtil.isBlank(gUid) || StrUtil.isBlank(lsId) || StrUtil.isBlank(lsToken)) {
            throw new BizException("获取二维码异常");
        }
        return JdInfo.builder()
                .sToken(sToken)
                .guid(gUid)
                .lsId(lsId)
                .lsToken(lsToken)
                .preCookie(StrUtil.format("guid={}; lang=chs; lsid={}; lstoken={};", gUid, lsId, lsToken))
                .ua(ua)
                .build();
    }

    /**
     * 获取一些必要的token信息
     *
     * @return JdInfo
     * @throws Exception e
     */
    @Deprecated
    private JdInfo loginEntrance() throws Exception {
        long currMs = System.currentTimeMillis();
        String loginEntranceUrl = "https://plogin.m.jd.com/cgi-bin/mm/new_login_entrance?lang=chs&appid=300&returnurl=https://wq.jd.com/passport/LoginRedirect?state=" + currMs
                + "&returnurl=https://home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&/myJd/home.action&source=wq_passport&_t=" + currMs;
        HttpResponse resp = HttpRequest.get(loginEntranceUrl)
                .header("Connection", "Keep-Alive")
                .header("Content-Type", ContentType.FORM_URLENCODED.getValue())
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Language", "zh-cn")
                .header("Referer", URLEncoder.encode("https://plogin.m.jd.com/login/login?appid=300&returnurl=https://wq.jd.com/passport/LoginRedirect?state=" + currMs
                        + "&returnurl=https://home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&/myJd/home.action&source=wq_passport", "utf-8"))
                .header("User-Agent", randomUa())
                .header("Host", "plogin.m.jd.com")
                .execute();
        String sToken = JSONObject.parseObject(resp.body()).getString("s_token");
        Map<String, String> setCookieKv = transSetCookie2Map(resp.headerList("Set-Cookie"));
        String gUid = setCookieKv.get("guid");
        String lsId = setCookieKv.get("lsid");
        String lsToken = setCookieKv.get("lstoken");
        log.info(JSONObject.toJSONString(resp.headerList("Set-Cookie")));
        if (StrUtil.isBlank(gUid) || StrUtil.isBlank(lsId) || StrUtil.isBlank(lsToken)) {
            throw new BizException("获取二维码异常");
        }
        return JdInfo.builder()
                .sToken(sToken)
                .guid(gUid)
                .lsId(lsId)
                .lsToken(lsToken)
                .preCookie(StrUtil.format("guid={}; lang=chs; lsid={}; lstoken={};", gUid, lsId, lsToken))
                .build();
    }

    /**
     * 根据token信息获取二维码地址
     *
     * @param info JdInfo
     * @throws Exception e
     */
    private void getQrCodeUrl(JdInfo info) throws Exception {
        long currMs = System.currentTimeMillis();
        String tokenUrl = "https://plogin.m.jd.com/cgi-bin/m/tmauthreflogurl?s_token=" + info.getSToken() + "&v=" + currMs + "&remember=true";
        JSONObject param = new JSONObject();
        param.put("lang", "chs");
        param.put("appid", 300);
        param.put("returnurl", "https://wqlogin2.jd.com/passport/LoginRedirect?state=" + currMs + "&returnurl=//home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&/myJd/home.action");
        param.put("source", "wq_passport");
        HttpResponse tokenResp = HttpRequest.post(tokenUrl)
                .body(param.toJSONString(), ContentType.FORM_URLENCODED.getValue())
                .form("lang", "chs")
                .form("appid", 300)
                .form("source", "wq_passport")
                .form("returnurl", URLEncoder
                        .encode("https://wqlogin2.jd.com/passport/LoginRedirect?state=" + currMs + "&returnurl=//home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&/myJd/home.action", "utf-8"))
                .header("Connection", "Keep-Alive")
                .header("Content-Type", ContentType.FORM_URLENCODED.getValue())
                .header("Accept", "application/json, text/plain, */*")
                .header("Cookie", info.getPreCookie())
                .header("Referer", "https://plogin.m.jd.com/login/login?appid=300&returnurl=https://wqlogin2.jd.com/passport/LoginRedirect?state=" + currMs
                        + "&returnurl=//home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&/myJd/home.action&source=wq_passport")
                .header("User-Agent", info.getUa())
                .header("Host", "plogin.m.jd.com")
                .execute();
        Map<String, String> setCookieKv = transSetCookie2Map(tokenResp.headerList("Set-Cookie"));
        info.setOklToken(setCookieKv.get("okl_token"));
        String token = JSONObject.parseObject(tokenResp.body()).getString("token");
        if (StrUtil.isBlank(token)) {
            throw new BizException("获取二维码异常");
        }
        info.setToken(token);
        String qrCodeUrl = "https://plogin.m.jd.com/cgi-bin/m/tmauth?appid=300&client_type=m&token=" + token;
        info.setQrCodeUrl(qrCodeUrl);
    }

    /**
     * content 转Base64图片
     *
     * @param content 内容
     * @return Base64
     */
    private String trans2ImgBase64(String content) {
        final String userDir = System.getProperty("user.dir");
        File qrCodeFile = FileUtil.file(userDir + File.separator + "jdQrCode.jpg");
        final File file = QrCodeUtil.generate(content, 300, 300, qrCodeFile);
        byte[] b;
        try {
            b = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            return Base64.getEncoder().encodeToString(b);
        } catch (IOException e) {
            return null;
        } finally {
            FileUtil.del(qrCodeFile);
        }
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

    /**
     * 时间戳UA
     *
     * @return jdapp ua
     */
    private String randomUa() {
        long l = System.currentTimeMillis();
        return StrUtil.format("jdapp;android;10.0.5;11;{}-{};network/wifi;model/M2102K1C;osVer/30;appBuild/88681;partner/lc001;eufv/1;jdSupportDarkMode/0;Mozilla/5.0 (Linux; Android 11; M2102K1C Build/RKQ1.201112.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045534 Mobile Safari/537.36", l, l);
    }

}
