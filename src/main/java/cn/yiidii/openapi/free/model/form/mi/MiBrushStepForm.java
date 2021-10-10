package cn.yiidii.openapi.free.model.form.mi;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * MiBrushStepForm
 *
 * @author YiiDii Wang
 * @create 2021-10-10 21:58
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MiBrushStepForm {

    @NotNull(message = "密码不能为空")
    @Pattern(regexp = "^(?:(?:\\+|00)86)?1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    @NotNull(message = "密码不能为空")
    private String password;

    private Long step = 18888L;

    private boolean auto = false;

}
