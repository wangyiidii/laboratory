package cn.yiidii.openapi.free.mapper;

import cn.yiidii.openapi.free.model.bo.system.AccessOverviewBO;
import cn.yiidii.openapi.free.model.bo.system.AccessRecordBO;
import cn.yiidii.openapi.free.model.bo.system.AccessTrendBO;
import cn.yiidii.openapi.free.model.entity.system.AccessRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 访问记录mapper
 *
 * @author YiiDii Wang
 * @create 2021-07-31 23:34
 */
@Mapper
public interface AccessRecordMapper extends BaseMapper<AccessRecord> {

    /**
     * 访问记录统计
     *
     * @param groupList 分组
     * @param topN      topN
     * @return AccessRecordBO
     */
    List<AccessRecordBO> statistics(@Param("groups") List<String> groupList, @Param("topN") Integer topN);

    /**
     * 访问趋势图
     *
     * @return AccessTrendBO
     */
    List<AccessTrendBO> accessTrend();

    /**
     * 访问概览
     *
     * @param path 开始时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return AccessOverviewBO
     */
    AccessOverviewBO getAccessOverview(@Param("path")String path, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}
