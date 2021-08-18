package cn.yiidii.openapi.netty.agent.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author YiiDii Wang
 * @create 2021-08-16 12:10
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "pigeon.netty.agent.server")
public class AgentServerConfig {

    private int port;
    private long idleTime;
}
