package cn.yiidii.openapi.job.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XxlJob 配置
 *
 * @author YiiDii Wang
 * @create 2021-10-10 15:04
 */
@Slf4j
@Configuration
@RefreshScope
public class XxlJobConfiguration {

    @Value("${pigeon.job.admin.addresses}")
    private String adminAddresses;

    @Value("${pigeon.job.executor.appname}")
    private String appname;

    @Value("${pigeon.job.executor.ip}")
    private String ip;

    @Value("${pigeon.job.executor.port}")
    private int port;

    @Value("${pigeon.job.accessToken}")
    private String accessToken;

    @Value("${pigeon.job.executor.logpath}")
    private String logPath;

    @Value("${pigeon.job.executor.logretentiondays}")
    private int logRetentionDays;

    /**
     * xxlJob Executor
     *
     * @return XxlJobExecutor
     */
    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobExecutor = new XxlJobSpringExecutor();
        xxlJobExecutor.setAdminAddresses(adminAddresses);
        xxlJobExecutor.setAppname(appname);
        xxlJobExecutor.setIp(ip);
        xxlJobExecutor.setPort(port);
        xxlJobExecutor.setAccessToken(accessToken);
        xxlJobExecutor.setLogPath(logPath);
        xxlJobExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobExecutor;
    }

}
