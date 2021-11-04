package cn.yiidii.openapi.free.model.vo.system;

import cn.yiidii.pigeon.common.core.base.enumeration.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 字典VO
 *
 * @author YiiDii Wang
 * @create 2021-11-04 10:17
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DictionaryVO {

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 类型标签
     */
    @ApiModelProperty(value = "类型标签")
    private String label;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    private String code;

    /**
     * 值
     */
    @ApiModelProperty(value = "值")
    private String value;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String desc;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortValue;

    /**
     * 内置
     */
    @ApiModelProperty(value = "内置")
    private Boolean readonly;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Status status;

}
