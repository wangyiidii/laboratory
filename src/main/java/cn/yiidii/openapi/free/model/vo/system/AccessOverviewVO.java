package cn.yiidii.openapi.free.model.vo.system;

import cn.yiidii.openapi.free.model.bo.system.AccessOverviewBO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 访问概览VO
 *
 * @author YiiDii Wang
 * @create 2021-08-05 23:44
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccessOverviewVO {

    private AccessOverviewBO today;
    private AccessOverviewBO yesterday;
    private AccessOverviewBO all;

}
