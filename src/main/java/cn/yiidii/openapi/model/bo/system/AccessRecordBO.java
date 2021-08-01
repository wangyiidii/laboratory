package cn.yiidii.openapi.model.bo.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 访问记录统计BO
 *
 * @author YiiDii Wang
 * @create 2021-08-01 10:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccessRecordBO {

    private String platform;
    private String browser;
    private String deviceName;
    private Integer count;
    private Integer total;
    private Double rate;

}
