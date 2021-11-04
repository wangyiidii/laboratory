package cn.yiidii.openapi.free.model.form.system;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Dictionary 保存表单
 *
 * @author YiiDii Wang
 * @create 2021-11-04 11:44
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DictionarySaveForm {

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @NotNull(message = "类型不能为空")
    private String type;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    private String code;

    /**
     * 值
     */
    @ApiModelProperty(value = "值")
    @NotNull(message = "值不能为空")
    private String value;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String desc;

}
