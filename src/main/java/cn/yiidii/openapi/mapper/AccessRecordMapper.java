package cn.yiidii.openapi.mapper;

import cn.yiidii.openapi.model.bo.system.AccessRecordBO;
import cn.yiidii.openapi.model.bo.system.AccessTrendBO;
import cn.yiidii.openapi.model.entity.system.AccessRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
     * @return AccessRecordBO
     */
    List<AccessRecordBO> statistics(@Param("groups") List<String> groupList);

    /**
     * 访问趋势图
     *
     * @return AccessTrendBO
     */
    List<AccessTrendBO> accessTrend();

}
