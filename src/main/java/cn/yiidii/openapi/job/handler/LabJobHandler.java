package cn.yiidii.openapi.job.handler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * LabJobHandler
 *
 * @author YiiDii Wang
 * @create 2021-10-10 18:44
 */
@Slf4j
@Component
public class LabJobHandler {

    @XxlJob("testJob")
    public ReturnT<?> testJob() {
        log.error("testJob");
        return ReturnT.SUCCESS;
    }

}
