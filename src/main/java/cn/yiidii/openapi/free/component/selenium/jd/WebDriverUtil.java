package cn.yiidii.openapi.free.component.selenium.jd;

import cn.hutool.core.util.StrUtil;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * WebDriver工具类
 *
 * @author YiiDii Wang
 * @create 2021-09-23 22:24
 */
public class WebDriverUtil {

    /**
     * 获取cookie
     *
     * @param driver WebDriver
     * @param name   cookie name
     * @return value
     */
    public static String getCookieByName(WebDriver driver, String name) {
        try {
            return driver.manage().getCookieNamed(name).getValue();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 判断元素是否存咋
     *
     * @param driver WebDriver
     * @param by     By
     * @return boolean
     */
    public static boolean isElementExist(WebDriver driver, By by) {
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        try {
            WebElement element = driver.findElement(by);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            return Objects.nonNull(element);
        } catch (Exception exception) {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            return false;
        }
    }

    /**
     * 点击按钮
     *
     * @param driver driver
     * @param by     by
     */
    public static void click(WebDriver driver, By by) {
        if (!isElementExist(driver, by)) {
            return;
        }
        WebElement ele = driver.findElement(by);
        ele.click();
    }

    /**
     * 选中
     *
     * @param driver driver
     * @param by     by
     */
    public static void select(WebDriver driver, By by) {
        if (!isElementExist(driver, by)) {
            return;
        }
        WebElement ele = driver.findElement(by);
        if (!ele.isSelected()) {
            ele.click();
        }
    }

    /**
     * 输入内容
     *
     * @param driver driver
     * @param by     by
     * @param value  value
     * @param append 追加
     */
    public static void input(WebDriver driver, By by, String value, boolean append) {
        if (!isElementExist(driver, by)) {
            return;
        }
        WebElement ele = driver.findElement(by);
        if (append) {
            ele.sendKeys(value);
        } else {
            ele.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
        }
    }

    /**
     * 获取元素属性
     *
     * @param ele      元素
     * @param attrName 属性名称
     * @return 属性值
     */
    public static String attr(WebElement ele, String attrName) {
        if (Objects.isNull(ele)) {
            return null;
        }
        String val = ele.getAttribute(attrName);
        return StrUtil.isBlank(val) ? null : val;
    }

    /**
     * 是否包含css
     *
     * @param ele 元素
     * @param css css名称
     * @return boolean
     */
    public static boolean containsCss(WebElement ele, String css) {
        if (Objects.isNull(ele)) {
            return false;
        }
        String classes = attr(ele, "class");
        if (StrUtil.isBlank(classes)) {
            return false;
        }
        return Stream.of(classes.split(" ")).anyMatch(e -> StrUtil.equals(e.trim(), css));
    }


    /**
     * 获取value
     *
     * @param driver driver
     * @param by     by
     * @return value
     */
    public static String getValue(WebDriver driver, By by) {
        if (!isElementExist(driver, by)) {
            return "";
        }
        WebElement ele = driver.findElement(by);
        return ele.getAttribute("value");
    }

    /**
     * 获取text
     *
     * @param driver driver
     * @param by     by
     * @return text
     */
    public static String getText(WebDriver driver, By by) {
        if (!isElementExist(driver, by)) {
            return "";
        }
        WebElement ele = driver.findElement(by);
        return ele.getText();
    }

}
