package cn.yiidii.openapi.oss.model.enumeration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 附件类型
 *
 * @author YiiDii Wang
 * @date 2021/3/9 22:24:51
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "DataType", description = "数据类型-枚举")
public enum DataType {

    /**
     * 图片
     */
    IMAGE("IMAGE", "图片"),
    /**
     * 视频
     */
    VIDEO("VIDEO", "视频"),
    /**
     * 音频
     */
    AUDIO("AUDIO", "音频"),
    /**
     * 文档
     */
    DOC("DOC", "文档"),
    /**
     * 其他
     */
    OTHER("OTHER", "其他"),
    ;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "描述")
    private String desc;


}
