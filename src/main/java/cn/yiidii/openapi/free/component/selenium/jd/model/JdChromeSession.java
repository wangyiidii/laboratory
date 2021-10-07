package cn.yiidii.openapi.free.component.selenium.jd.model;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.free.component.selenium.jd.WebDriverUtil;
import cn.yiidii.openapi.free.service.exception.jd.JdException;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * JD chrome session model
 *
 * @author YiiDii Wang
 * @create 2021-09-23 02:39
 */
@Slf4j
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class JdChromeSession {

    private static Pattern pattern = Pattern.compile("data:image.*base64,(.*)");

    private String sessionId;
    private RemoteWebDriver remoteWebDriver;
    private LocalDateTime createTime;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     */
    public void sendSmsCode(String phone) {
        Assert.isTrue(PhoneUtil.isPhone(phone), "手机号码格式不正确");
        // 打开京东登录页
        this.remoteWebDriver.get("https://plogin.m.jd.com/login/login?appid=300&returnurl=https%3A%2F%2Fwq.jd.com%2Fpassport%2FLoginRedirect%3Fstate%3D1101624461975%26returnurl%3Dhttps%253A%252F%252Fhome.m.jd.com%252FmyJd%252Fnewhome.action%253Fsceneval%253D2%2526ufc%253D%2526&source=wq_passport");
        // 选中同意策略
        this.clickPolicy();
        // ctrl + a 输入手机号
        WebDriverUtil.input(this.getRemoteWebDriver(), By.xpath("//input[@type='tel']"), phone, false);
        // 点击发送验证码
        clickSendSmsCode();
        // check
        checkDialog();
    }

    /**
     * 登录
     *
     * @param code 验证码
     */
    public void login(String code) {
        checkDialog();
        // ctrl + a 输入验证码
        WebDriverUtil.input(this.getRemoteWebDriver(), By.id("authcode"), code, false);
        // 选中同意策略
        this.clickPolicy();
        // 点击登录
        WebDriverUtil.click(this.getRemoteWebDriver(), By.xpath("//a[@report-eventid='MLoginRegister_SMSLogin']"));
        // 2秒的时间, 等待toast, 主要是验证码错误的弹框
        WebDriverWait wait = new WebDriverWait(this.remoteWebDriver, 2, 100);
        try {
            WebElement msgContainer = wait.until(driver -> driver.findElement(By.className("msg_container")));
            throw new JdException(-1, msgContainer.getText());
        } catch (Exception exception) {
            log.error(StrUtil.format("未发现登录异常的toast"));
        }
        checkDialog();
    }


    /**
     * 检查弹窗
     * <p/>
     * dialog点击确定, 部分抛出异常（例如: 短信验证码发送次数已达上限 等）
     */
    public void checkDialog() {
        // 可能会弹框, 点击确定; 部分抛出异常
        By dialogDesBy = By.className("dialog-des");
        boolean isDialogDescExist = WebDriverUtil.isElementExist(this.getRemoteWebDriver(), dialogDesBy);
        if (isDialogDescExist) {
            // 描述ele
            WebElement dialogDescEle = this.remoteWebDriver.findElement(dialogDesBy);
            // 确定ele
            WebElement dialogSureEle = this.remoteWebDriver.findElement(By.xpath("//button[@class='dialog-sure']"));
            // 描述
            String dialogDesc = dialogDescEle.getText();
            log.error(StrUtil.format("dialogDesc: {}", dialogDesc));
            if (StrUtil.containsAnyIgnoreCase(dialogDesc,
                    "对不起，短信验证码发送次数已达上限，请24小时后再试。")) {
                throw new JdException(-1, dialogDesc);
            }
            // 点击确定
            dialogSureEle.click();
        }
    }

    /**
     * 点击选中同意策略
     */
    private void clickPolicy() {
        WebDriverUtil.select(this.getRemoteWebDriver(), By.xpath("//input[@class='policy_tip-checkbox']"));
    }

    /**
     * 点击获取验证码
     */
    private void clickSendSmsCode() {
        WebElement sendSmsCodeBtn = this.remoteWebDriver.findElement(By.xpath("//button[@report-eventid='MLoginRegister_SMSReceiveCode']"));
        boolean isActive = WebDriverUtil.containsCss(sendSmsCodeBtn, "active");
        if (isActive) {
            sendSmsCodeBtn.click();
            return;
        }
        throw new JdException(-1, "无法发送验证码, 请重新获取试试看!");
    }

}
