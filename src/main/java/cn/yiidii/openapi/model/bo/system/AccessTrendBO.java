package cn.yiidii.openapi.model.bo.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 访问记录趋势图
 *
 * @author YiiDii Wang
 * @create 2021-08-01 18:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccessTrendBO {

    private Integer hour;
    private Integer num;

}
