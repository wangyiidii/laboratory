package cn.yiidii.openapi;

import cn.yiidii.pigeon.common.knife4j.annotation.EnablePigeonKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Openapi启动类
 *
 * @author YiiDii Wang
 * @date 2021/6/1 9:44:38
 */
@SpringBootApplication
@ComponentScan(basePackages = "cn.yiidii")
@EnablePigeonKnife4j
public class OpenapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenapiApplication.class, args);
    }

}
