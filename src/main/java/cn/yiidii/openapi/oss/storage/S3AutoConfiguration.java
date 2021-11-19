package cn.yiidii.openapi.oss.storage;

import cn.yiidii.openapi.oss.properties.OssProperties;
import cn.yiidii.openapi.oss.strategy.FileStrategy;
import cn.yiidii.openapi.oss.strategy.service.impl.S3FileStrategyImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 兼容s3协议的云存储通用配置
 *
 * @author YiiDii Wang
 * @create 2021-03-09 22:02
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = OssProperties.PREFIX, name = "type", havingValue = "s3")
public class S3AutoConfiguration {

    @Bean
    public FileStrategy getFileStrategy(OssProperties ossProperties) {
        return new S3FileStrategyImpl(ossProperties);
    }

}
