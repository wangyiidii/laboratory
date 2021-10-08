package cn.yiidii.openapi.free.model.form;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * @author YiiDii Wang
 * @create 2021-10-08 13:02
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MiBrushStepForm {


    @ApiModelProperty(value = "手机号")
    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "^(?:(?:\\+|00)86)?1\\d{10}$", message = "手机号格式不正确")
    public String phone;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空")
    public String password;

    @ApiModelProperty(value = "步数")
    public long step = 18888L;


}
