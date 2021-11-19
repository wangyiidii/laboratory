package cn.yiidii.openapi.oss.properties;

import java.io.Serializable;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author YiiDii Wang
 * @create 2021-03-09 21:54
 */
@Data
@Configuration
@ConfigurationProperties(prefix = OssProperties.PREFIX)
public class OssProperties implements Serializable {

    public static final String PREFIX = "pigeon.file";

    /**
     * 类型
     */
    private String type;

    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String customDomain;

    /**
     * 默认的存储桶名称
     */
    private String bucketName = "pigeon";

    /**
     * 区域
     */
    private String region;

    /**
     * Access key
     */
    private String accessKey;

    /**
     * Secret key
     */
    private String secretKey;

    /**
     * true path-style nginx 反向代理和S3默认支持 pathStyle {http://endpoint/bucketname} false
     * supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style
     * 模式{http://bucketname.endpoint}
     */
    private Boolean pathStyleAccess = true;



}
