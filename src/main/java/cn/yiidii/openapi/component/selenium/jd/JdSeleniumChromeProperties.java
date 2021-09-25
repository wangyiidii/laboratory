package cn.yiidii.openapi.component.selenium.jd;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * chrome jd 配置
 *
 * @author YiiDii Wang
 * @create 2021-09-24 17:03
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "pigeon.jd.selenium.chrome")
public class JdSeleniumChromeProperties {

    private String driverUrl;
    private Session session = new Session();

    @Data
    public static class Session {

        private int maxNum = 10;
        private int timeout = 60 * 2;
    }
}
