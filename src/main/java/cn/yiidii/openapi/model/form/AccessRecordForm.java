package cn.yiidii.openapi.model.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 访问记录表单
 *
 * @author YiiDii Wang
 * @create 2021-08-06 20:14
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccessRecordForm {

    private String path;
    private String queryString;
    private String uaStr;

}
