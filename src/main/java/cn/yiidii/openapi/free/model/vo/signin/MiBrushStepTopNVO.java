package cn.yiidii.openapi.free.model.vo.signin;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * MiBrushStepTopNVO
 *
 * @author YiiDii Wang
 * @create 2021-11-11 09:40
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MiBrushStepTopNVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("操作内容")
    private String content;

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("地区")
    private String location;

    @ApiModelProperty("创建时间")
    private LocalDateTime startTime;

}
