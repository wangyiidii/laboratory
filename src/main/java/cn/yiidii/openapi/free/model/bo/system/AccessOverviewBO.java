package cn.yiidii.openapi.free.model.bo.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 访问统计
 *
 * @author YiiDii Wang
 * @create 2021-08-05 23:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccessOverviewBO {

    private Integer pv;
    private Integer uv;
    private Integer ipCount;

}
