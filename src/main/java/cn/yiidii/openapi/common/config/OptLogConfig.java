package cn.yiidii.openapi.common.config;

import cn.yiidii.openapi.free.mongodao.OptLogDAO;
import cn.yiidii.pigeon.log.event.LogListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 日志配置
 *
 * @author YiiDii Wang
 * @create 2021-11-11 13:31
 */
@Configuration
public class OptLogConfig {

    @Bean
    @ConditionalOnExpression("${pigeon.log.enabled:true} && 'MONGO'.equals('${pigeon.log.type:LOGGER}')")
    public LogListener loggerSysLogListener(OptLogDAO optLogDAO) {
        return new LogListener(optLogDAO::save);
    }
}
