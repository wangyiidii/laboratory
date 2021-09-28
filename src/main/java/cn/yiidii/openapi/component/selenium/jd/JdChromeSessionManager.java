package cn.yiidii.openapi.component.selenium.jd;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.component.selenium.jd.model.JdChromeSession;
import cn.yiidii.openapi.model.vo.JdChromeSessionVO;
import cn.yiidii.openapi.service.exception.jd.JdException;
import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * session 管理
 *
 * @author YiiDii Wang
 * @create 2021-09-23 02:35
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JdChromeSessionManager {

    private static final Map<String, JdChromeSession> SESSION_MAP = new ConcurrentHashMap<>();
    private static final ChromeOptions DFT_CHROME_OPTIONS = new ChromeOptions();

    private final JdSeleniumChromeProperties jdProperty;

    @PostConstruct
    public void init() {
        // 无痕模式
        DFT_CHROME_OPTIONS.addArguments("--incognito");
        // 手机模式
        Map<String, String> mobileEmulation = Maps.newHashMap();
        mobileEmulation.put("deviceName", "iPhone X");
        DFT_CHROME_OPTIONS.setExperimentalOption("mobileEmulation", mobileEmulation);
    }

    /**
     * 获取缓存的chrome session
     *
     * @param sid sessionId
     * @return JdChromeSession
     */
    public JdChromeSession get(String sid) {
        if (SESSION_MAP.containsKey(sid)) {
            return SESSION_MAP.get(sid);
        }
        throw new JdException(-1, "会话已过期, 请尝试重新获取验证码!");
    }

    public void remove(String sid) {
        if (StrUtil.isBlank(sid)) {
            return;
        }
        SESSION_MAP.remove(sid);
    }

    /**
     * 获取session Map
     *
     * @return session map
     */
    public Map<String, JdChromeSession> getAllSession() {
        return SESSION_MAP;
    }

    /**
     * 创建一个新的session
     *
     * @return JdChromeSession  session
     * @throws MalformedURLException MalformedURLException
     */
    public JdChromeSession newSession() throws MalformedURLException {
        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(new URL(jdProperty.getDriverUrl()), DFT_CHROME_OPTIONS);
        remoteWebDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        JdChromeSession session = new JdChromeSession()
                .setSessionId(RandomUtil.randomStringUpper(12))
                .setRemoteWebDriver(remoteWebDriver)
                .setCreateTime(LocalDateTime.now());
        SESSION_MAP.put(session.getSessionId(), session);
        return session;
    }


    /**
     * 发送验证码
     *
     * @param sid   sessionId
     * @param phone 手机号
     * @return JdChromeSessionVO
     */
    public JdChromeSessionVO sendSmsCode(String sid, String phone) throws MalformedURLException {
        Assert.isTrue(PhoneUtil.isPhone(phone), "手机号码格式不正确");
        // 会话数量限制
        boolean sessionNumLimit = (StrUtil.isBlank(sid) || !this.getAllSession().containsKey(sid)) && this.getAllSession().size() >= jdProperty.getSession().getMaxNum();
        if (sessionNumLimit) {
            throw new JdException(-1, "会话连接达到上限, 稍后再试吧~");
        }
        // 获取session
        JdChromeSession session;
        if (Objects.isNull(sid) || !this.getAllSession().containsKey(sid)) {
            // 没有传sid或者缓存没有的话, 新建一个
            session = this.newSession();
        } else {
            // 否则直接用缓存的
            session = this.get(sid);
        }
        // 发送验证码
        session.sendSmsCode(phone);
        // 返回vo
        return BeanUtil.toBean(session, JdChromeSessionVO.class);
    }

    /**
     * 登录
     *
     * @param sid  sessionId
     * @param code code
     * @return JdChromeSessionVO
     */
    public JdChromeSessionVO login(String sid, String code) {
        // 获取session
        JdChromeSession session = this.get(sid);
        // 登录
        session.login(code);
        // 返回VO
        return getCookie(sid);
    }

    /**
     * 获取cookie
     *
     * @param sid sessionId
     * @return JdChromeSessionVO
     */
    public JdChromeSessionVO getCookie(String sid) {
        JdChromeSession session = this.get(sid);
        RemoteWebDriver webDriver = session.getRemoteWebDriver();
        // 等待20
        WebDriverWait wait = new WebDriverWait(webDriver, 5);
        try {
            wait.until(ExpectedConditions.titleContains("多快好省"));
        } catch (Exception exception) {
            throw new JdException(-1, "登录失败");
        }
        // 尝试获取pt_key和pt_pin
        String ptKey = WebDriverUtil.getCookieByName(webDriver, "pt_key");
        String ptPin = WebDriverUtil.getCookieByName(webDriver, "pt_pin");
        if (StrUtil.isBlank(ptKey) || StrUtil.isBlank(ptPin)) {
            // pt_key和pt_pin存在为空的视为没有登陆成功
            String sendAuthCodeBtnRule = "//button[@report-eventid='MLoginRegister_SMSReceiveCode']";

            if (WebDriverUtil.isElementExist(webDriver, By.xpath(sendAuthCodeBtnRule))) {
                WebElement sendAuthCodeBtn = webDriver.findElement(By.xpath(sendAuthCodeBtnRule));
                String reSendAuthCodeSecRegx = "重新获取\\((\\d+)s\\)";
                String sec = ReUtil.get(reSendAuthCodeSecRegx, sendAuthCodeBtn.getText(), 0);
                System.out.println(StrUtil.format("重新发送({})", sec));
            }
            boolean isInLoginPage = session.getRemoteWebDriver().getTitle().contains("京东登录注册");
            throw new JdException(-1, isInLoginPage ? "验证码错误" : "获取ck异常");
        }
        String ck = StrUtil.format("pt_key={}; pt_pin={}", ptKey, ptPin);
        JdChromeSessionVO vo = BeanUtil.toBean(session, JdChromeSessionVO.class);
        vo.setCk(ck);
        // 打印日志
        String phone = WebDriverUtil.getValue(webDriver, By.xpath("//input[@type='tel']"));
        if (StrUtil.isNotBlank(phone)) {
            log.info(StrUtil.format("{}获取了cookie", DesensitizedUtil.mobilePhone(phone)));
        }
        // 登录获取cookie完成, 从map中移除session
        this.remove(sid);
        return vo;
    }

    /**
     * 移除无用的session
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    @Async("scheduledExecutor")
    public void rmUnusedSession() {
        // 过滤超出property中配置session超时时间的sid
        List<String> rm = this.getAllSession()
                .entrySet()
                .stream()
                .filter(e -> Duration.between(e.getValue().getCreateTime(), LocalDateTime.now()).toMillis() / 1000 >= jdProperty.getSession().getTimeout())
                .map(Entry::getKey)
                .distinct()
                .collect(Collectors.toList());
        // 关闭chrome
        rm.forEach(e -> {
            try {
                this.get(e).getRemoteWebDriver().close();
            } catch (Exception ex) {
                log.error(StrUtil.format("关闭chrome异常: {}", ex.getMessage()));
            }
            this.getAllSession().remove(e);
        });
        // 日志
        if (CollUtil.isNotEmpty(rm)) {
            log.info(StrUtil.format("定时移除{}个无用session", rm.size()));
        }
    }

}
