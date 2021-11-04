package cn.yiidii.openapi.free.model.entity.system;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

import cn.yiidii.pigeon.common.core.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 字典
 *
 * @author YiiDii Wang
 * @create 2021-11-03 22:17
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(of = {"type", "code"})
@Accessors(chain = true)
@TableName("dictionary")
@ApiModel(value = "Dictionary", description = "字典")
@AllArgsConstructor
public class Dictionary extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @NotEmpty(message = "类型不能为空")
    @Size(max = 255, message = "类型长度不能超过255")
    @TableField(value = "type", condition = LIKE)
    private String type;

    /**
     * 类型标签
     */
    @ApiModelProperty(value = "类型标签")
    @NotEmpty(message = "类型标签不能为空")
    @Size(max = 255, message = "类型标签长度不能超过255")
    @TableField(value = "label", condition = LIKE)
    private String label;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    @NotEmpty(message = "编码不能为空")
    @Size(max = 64, message = "编码长度不能超过64")
    @TableField(value = "code", condition = LIKE)
    private String code;

    /**
     * 值
     */
    @ApiModelProperty(value = "值")
    @NotEmpty(message = "值不能为空")
    @Size(max = 64, message = "值长度不能超过64")
    @TableField(value = "value", condition = LIKE)
    private String value;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Size(max = 255, message = "描述长度不能超过255")
    @TableField(value = "`desc`", condition = LIKE)
    private String desc;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @TableField("sort_value")
    private Integer sortValue;

    /**
     * 内置
     */
    @ApiModelProperty(value = "内置")
    @TableField("readonly_")
    private Boolean readonly;

}
