package cn.yiidii.openapi.service;

import cn.yiidii.openapi.model.bo.system.AccessOverviewBO;
import cn.yiidii.openapi.model.bo.system.AccessRecordBO;
import cn.yiidii.openapi.model.bo.system.AccessTrendBO;
import cn.yiidii.openapi.model.entity.system.AccessRecord;
import cn.yiidii.openapi.model.vo.AccessOverviewVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 访问记录业务接口
 *
 * @author YiiDii Wang
 * @create 2021-07-31 23:32
 */
public interface IAccessRecordService extends IService<AccessRecord> {

    /**
     * 添加一条记录
     *
     * @param uaStr ua
     * @return AccessRecord
     */
    AccessRecord addOne(String uaStr);

    /**
     * 访问统计
     *
     * @param group group
     * @param topN  topN
     * @return AccessRecordBO
     */
    List<AccessRecordBO> statistic(String group, Integer topN);

    /**
     * 访问趋势图
     *
     * @return AccessTrendBO
     */
    List<AccessTrendBO> accessTrend();

    /**
     * 访问概览
     *
     * @return AccessOverviewBO
     */
    AccessOverviewVO getAccessOverview();
}
