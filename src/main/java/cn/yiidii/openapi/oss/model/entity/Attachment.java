package cn.yiidii.openapi.oss.model.entity;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

import cn.yiidii.pigeon.common.core.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

/**
 * 附件
 *
 * @author YiiDii Wang
 * @date 2021/3/9 23:14:44
 */
@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("attachment")
@ApiModel(value = "Attachment", description = "附件")
@AllArgsConstructor
public class Attachment extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 域名
     */
    @ApiModelProperty(value = "域名")
    @TableField(value = "domain")
    private String domain;

    /**
     * 文件访问链接
     */
    @ApiModelProperty(value = "文件访问链接")
    @Length(max = 255, message = "文件访问链接长度不能超过255")
    @TableField(value = "url", condition = LIKE)
    private String url;

    /**
     * 唯一文件名
     */
    @ApiModelProperty(value = "唯一文件名")
    @Length(max = 255, message = "唯一文件名长度不能超过255")
    @TableField(value = "filename", condition = LIKE)
    private String filename;

    /**
     * 后缀 (没有.)
     */
    @ApiModelProperty(value = "后缀")
    @Length(max = 64, message = "后缀长度不能超过64")
    @TableField(value = "suffix", condition = LIKE)
    private String suffix;

    /**
     * 大小
     */
    @ApiModelProperty(value = "大小")
    @TableField("size")
    private Long size;

}
